package se.pensionsmyndigheten.icc.test.orderprocessor;

import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.main.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.pensionsmyndigheten.icc.test.orderprocessor.route.Route;

public class Application {

    private static final String DEFAULT_PROPERTIES_LOCATION = "classpath:config/application.properties";

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String... args){
        LOG.info("Starting application...");
        String location = System.getProperty("properties.location",DEFAULT_PROPERTIES_LOCATION);
        LOG.info("Loading properties from '{}'.",location);

        Main camel = new Main();
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation(location);
        camel.bind("properties",pc);
        camel.addRouteBuilder(new Route());
        try {
            camel.run();
        }
        catch(Exception e){
            LOG.error("Failed to start application.",e);
        }
    }

}
