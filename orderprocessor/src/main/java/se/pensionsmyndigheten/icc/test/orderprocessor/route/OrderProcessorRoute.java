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

	private static final int MAX_ORDERS_PER_FILE = 2;

	@Override
	public void configure() throws Exception {

		onException(ValidationException.class)
				.setHeader("CamelFileName", simple("badorder.${header.CamelSplitIndex}.${header.CamelFileName}"))
				.to("properties:invalid.uri");

		// note, 'properties:somekey' could be replaced with '{{somekey}}'
//		  from("properties:source.uri")
//		  	.routeId(ROUTE_ID)
//		  	.streamCaching()
//		  	.log(LoggingLevel.INFO,"Processing file ${header.CamelFileName}")
//		  	.split().tokenizeXML("order").streaming() //
//		  	.log(LoggingLevel.INFO,"Split ${body}")
//		  	.aggregate(constant(true), new BodyAggregatingStrategy())
//		  	.completionSize(MAX_ORDERS_PER_FILE)
//		  	.completionInterval(500) // TODO: Fix orders file ... // TODO: Validate //
//		  	.log(LoggingLevel.INFO,"Validation success!") // TODO: Better file name
//		  	.setHeader("CamelFileName", simple("orders-${header.CamelSplitIndex}.xml"))
//		  	.to("properties:target.uri")
//		  	.log(LoggingLevel.INFO, "Wrote file ${header.CamelFileName}") 
//		  	.end();
		  
		Namespaces ns = new Namespaces("order", "http://test.icc.pensionsmyndigheten.se/order");

		from("properties:source.uri")
			.routeId(ROUTE_ID).streamCaching().split()
			.xtokenize("//order:order", 'w', ns)
			.streaming()
			.aggregate(constant(true), new BodyAggregatingStrategy())
			.completionSize(1)
			.completionInterval(500)
			.to("properties:validation.uri").id("validate-input")
			.setHeader("CamelFileName", simple("${header.CamelFileName}.${header.CamelSplitIndex}"))
			.to("properties:target.uri")
			.end();

	}

}
