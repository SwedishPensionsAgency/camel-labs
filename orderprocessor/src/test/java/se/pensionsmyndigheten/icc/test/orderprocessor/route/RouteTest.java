package se.pensionsmyndigheten.icc.test.orderprocessor.route;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ExchangePattern;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.validation.SchemaValidationException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class RouteTest extends CamelTestSupport {

    private static final String SOURCE_URI="direct:source";
    private static final String VALIDATION_URI = "validator:schema/orders.xsd";
    private static final String TARGET_URI="mock:target";
    private static final String GOOD_INPUT_FILE = "xml/allgood_orders.xml";
    private static final String BAD_INPUT_FILE = "xml/somebad_orders.xml";

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception{
        return new Route();
    }

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        Properties properties = new Properties();
        properties.setProperty("source.uri",SOURCE_URI);
        properties.setProperty("target.uri",TARGET_URI);
        properties.setProperty("validation.uri",VALIDATION_URI);
        return properties;
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        context.setAllowUseOriginalMessage(Boolean.FALSE);
        return context;
    }

    @Test
    public void testRouteWithOnlyGoodOrders(){
        //setup mocks
        MockEndpoint mockEndpoint = this.getMockEndpoint(TARGET_URI);
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.setResultWaitTime(200);

        //setup input
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(GOOD_INPUT_FILE).getFile());
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            //send message
            this.template.sendBody(SOURCE_URI, ExchangePattern.InOut, fileInputStream);
            //assert
            assertMockEndpointsSatisfied();
        }
        catch (Exception e){
            fail("Unexpected error was caught: "+e);
        }
    }

    @Test
    public void testRouteWithSomeBadOrders(){
        //setup mocks
        MockEndpoint mockEndpoint = this.getMockEndpoint(TARGET_URI);

        //setup input
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(BAD_INPUT_FILE).getFile());
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            //send message
            this.template.sendBody(SOURCE_URI, ExchangePattern.InOut, fileInputStream);
            fail("Expected an exception here.");
        }
        catch (CamelExecutionException e){
            Assert.assertTrue(e.getCause() instanceof SchemaValidationException);
        }
        catch (Exception e){
            fail("Unexpected exception was caught: "+e);
        }
    }

}
