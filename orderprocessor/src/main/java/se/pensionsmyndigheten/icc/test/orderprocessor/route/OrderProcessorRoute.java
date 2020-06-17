package se.pensionsmyndigheten.icc.test.orderprocessor.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

/**
 * Very simple order processor route to consume and validate orders from a configurable {@link org.apache.camel.Endpoint}
 * via the {@link org.apache.camel.component.properties.PropertiesComponent}.
 */
public class OrderProcessorRoute extends RouteBuilder {

    private static final String ROUTE_ID = "OrderProcessorRoute";

    @Override
    public void configure() throws Exception {

        //note, 'properties:somekey' could be replaced with '{{somekey}}'
        from("properties:source.uri")
                .routeId(ROUTE_ID)
                .streamCaching()
                .log(LoggingLevel.INFO,"Got something...")
                .to("properties:validation.uri")
                .log(LoggingLevel.INFO,"Validation success!")
                .to("properties:target.uri")
                .end();
    }

}
