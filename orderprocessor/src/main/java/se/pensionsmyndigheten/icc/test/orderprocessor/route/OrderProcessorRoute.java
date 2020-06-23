package se.pensionsmyndigheten.icc.test.orderprocessor.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;

import se.pensionsmyndigheten.icc.test.orderprocessor.strategy.BodyAggregatingStrategy;
import se.pensionsmyndigheten.icc.test.orderprocessor.strategy.OrderAggregatingStrategy;

/**
 * Very simple order processor route to consume and validate orders from a
 * configurable {@link org.apache.camel.Endpoint} via the
 * {@link org.apache.camel.component.properties.PropertiesComponent}.
 */
public class OrderProcessorRoute extends RouteBuilder {

	private static final String ORDER_ROUTE_VALIDATE_ID = "OrderProcessorRoute.Validate";
	private static final String ORDER_ROUTE_MERGE_ID = "OrderProcessorRoute.Merge";

	private static final int MAX_NR_OF_ORDERS = 500;
	
	private static final String FILE_OUT_PATTERN = "orders.${date:now:yyyyMMddHHmmssSSS}.${header.CamelSplitIndex}.xml";
	private static final String INVALID_FILE_OUT_PATTERN = "invalidorder.${date:now:yyyyMMddHHmmssSSS}.${header.CamelSplitIndex}.xml";

	@Override
	public void configure() throws Exception {

		onException(ValidationException.class)
				.setHeader("CamelFileName", simple(INVALID_FILE_OUT_PATTERN))
				.to("properties:invalid.uri");
		
		onException(Exception.class)
			.to("properties:invalid.uri");

		Namespaces ns = new Namespaces("order", "http://test.icc.pensionsmyndigheten.se/order");

		// Read one order file, split, validate and aggregate
		from("properties:source.uri")
			.routeId(ORDER_ROUTE_VALIDATE_ID)
			.streamCaching()
			.log(LoggingLevel.INFO, "Got file: ${file:name}")
			.split()
			.xtokenize("//order:order", 'w', ns)
			.streaming()
			.to("properties:validation.uri").id("validate-input")
			.aggregate(constant(true), new BodyAggregatingStrategy())
			.completionInterval(1000)
			.to("direct:mergeOrders")
			.end();
		
		// Split again, remove root element and aggregate to larger files with size MAX_NR_OF_ORDERS
		from("direct:mergeOrders")
			.id(ORDER_ROUTE_MERGE_ID)
			.streamCaching()
			.split()
			.tokenizeXML("order")
			.streaming()
			.aggregate(constant(true), new OrderAggregatingStrategy())
			.completionSize(MAX_NR_OF_ORDERS)
			.completionInterval(1000)
			.to("properties:validation.uri").id("validate-output")
			.setHeader("CamelFileName", simple(FILE_OUT_PATTERN))
			.to("properties:target.uri")
			.log(LoggingLevel.INFO, "Wrote file: ${file:name}")
			.end();
		
	}

}
