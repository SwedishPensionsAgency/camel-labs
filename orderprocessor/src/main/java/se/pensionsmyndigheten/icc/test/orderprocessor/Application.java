package se.pensionsmyndigheten.icc.test.orderprocessor;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.main.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.pensionsmyndigheten.icc.test.orderprocessor.route.OrderProcessorRoute;

/**
 * The main class to run the application.
 */
public class Application {

    private static final String DEFAULT_PROPERTIES_LOCATION = "classpath:config/application.properties";

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String... args){
        LOG.info("Starting...");
        //OOTB wrapper class for the CamelContext
        Main camel = new Main();
        //Setup the properties component to load properties from a configurable location or default
        camel.setPropertyPlaceholderLocations(System.getProperty("properties.location",
                DEFAULT_PROPERTIES_LOCATION));
        //Add the order processor route
        camel.addRouteBuilder(new OrderProcessorRoute());
        try {
            //Start the underlying CamelContext and keep it running
            camel.run();
        }
        catch(Exception e){
            LOG.error("Failed to start Camel.",e);
        }
    }

}
