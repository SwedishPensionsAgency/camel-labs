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

	private static final String ROUTE_ID = "OrderProcessorRoute";

	private static final int MAX_NR_OF_ORDERS = 2;
	
	private static final String FILE_OUT_PATTERN = "${file:onlyname.noext}.${header.CamelSplitIndex}.${file:name.ext}";

	@Override
	public void configure() throws Exception {

		onException(ValidationException.class)
				.setHeader("CamelFileName", simple(FILE_OUT_PATTERN))
				.to("properties:invalid.uri");

		Namespaces ns = new Namespaces("order", "http://test.icc.pensionsmyndigheten.se/order");

		from("properties:source.uri")
			.routeId(ROUTE_ID)
			.streamCaching()
			.split()
			.xtokenize("//order:order", 'w', ns)
			.streaming()
			.aggregate(constant(true), new BodyAggregatingStrategy())
			.completionSize(1)
			.completionInterval(500)
			.to("properties:validation.uri").id("validate-input")
			.setHeader("CamelFileName", simple(FILE_OUT_PATTERN))
			.to("properties:target.uri")
			.end();

	}

}
