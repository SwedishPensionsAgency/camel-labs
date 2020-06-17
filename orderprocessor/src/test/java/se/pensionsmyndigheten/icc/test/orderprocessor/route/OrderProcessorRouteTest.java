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

/**
 * Unit tests for {@link OrderProcessorRoute}.
 * <p/>
 * Uses {@link CamelTestSupport} to setup and start an underlying {@link CamelContext} during tests.
 */
public class OrderProcessorRouteTest extends CamelTestSupport {

    //route endpoints
    private static final String SOURCE_URI="direct:source";
    private static final String VALIDATION_URI = "validator:schema/orders.xsd";
    private static final String TARGET_URI="mock:target";

    //input files
    private static final String GOOD_INPUT_FILE = "xml/allgood_orders.xml";
    private static final String BAD_INPUT_FILE = "xml/somebad_orders.xml";

    //misc
    private static final int DEFAULT_RESULT_WAIT_TIME = 200;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception{
        //create the order processor route builder so we can test it
        return new OrderProcessorRoute();
    }

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        //configure the endpoints
        Properties properties = new Properties();
        properties.setProperty("source.uri",SOURCE_URI);
        properties.setProperty("target.uri",TARGET_URI);
        properties.setProperty("validation.uri",VALIDATION_URI);
        return properties;
    }

    @Test
    public void testRouteWithOnlyGoodOrders(){
        //setup mocks
        MockEndpoint mockEndpoint = this.createMockEndpoint(1);

        //setup input
        try(FileInputStream fileInputStream = new FileInputStream(loadFromClasspath(GOOD_INPUT_FILE))) {
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
        MockEndpoint mockEndpoint = this.createMockEndpoint(0);

        //setup input
        try(FileInputStream fileInputStream = new FileInputStream(loadFromClasspath(BAD_INPUT_FILE))) {
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

    private File loadFromClasspath(String file){
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(file).getFile());
    }

    private MockEndpoint createMockEndpoint(int expectedMessageCount){
        MockEndpoint result = this.getMockEndpoint(TARGET_URI);
        result.expectedMessageCount(expectedMessageCount);
        result.setResultWaitTime(DEFAULT_RESULT_WAIT_TIME);
        return result;
    }

}
