package se.pensionsmyndigheten.icc.test.orderprocessor.route;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Unit tests for {@link OrderProcessorRoute}.
 * <p/>
 * Uses {@link CamelTestSupport} to setup and start an underlying {@link CamelContext} during tests.
 */
public class OrderProcessorRouteTest extends CamelTestSupport {

    //route endpoints
    private static final String SOURCE_URI="direct:source";
    private static final String VALIDATION_URI = "file:schema/orders.xsd";
    private static final String TARGET_URI="mock:target";
    private static final String INVALID_URI="mock:invalid";

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
        properties.setProperty("invalid.uri",INVALID_URI);
        properties.setProperty("validation.uri",VALIDATION_URI);
        return properties;
    }

    @Test
    public void testRouteWithOnlyGoodOrders(){
        //setup mocks
        MockEndpoint mockEndpoint = this.createMockEndpoint(1, TARGET_URI);

        //setup input
        try(FileInputStream fileInputStream = new FileInputStream(loadFromClasspath(GOOD_INPUT_FILE))) {
            //send message
            this.template.sendBody(SOURCE_URI, ExchangePattern.InOut, fileInputStream);
            assertMockEndpointsSatisfied(3, TimeUnit.SECONDS);
        }
        catch (Exception e){
            fail("Unexpected error was caught: "+e);
        }
    }

//    @Test
    public void testRouteWithSomeBadOrders(){
        //setup mocks
    	MockEndpoint invalidOrderEndpoint = createMockEndpoint(3, INVALID_URI);
    	MockEndpoint validOrderEndpoint = createMockEndpoint(1, TARGET_URI);

    	try(FileInputStream fileInputStream = new FileInputStream(loadFromClasspath(BAD_INPUT_FILE))) {
            //send message
            System.out.println("A1");
            Thread.sleep(500);
            this.template.sendBody(SOURCE_URI, ExchangePattern.InOut, fileInputStream);
            System.out.println("A2");
            assertMockEndpointsSatisfied(15, TimeUnit.SECONDS);
    	}
        catch (Exception e){
            fail("Unexpected exception was caught: " +e);
        }
    }

    private File loadFromClasspath(String file){
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(file).getFile());
    }

    private MockEndpoint createMockEndpoint(int expectedMessageCount, String uri){
        MockEndpoint result = this.getMockEndpoint(uri);
        result.expectedMessageCount(expectedMessageCount);
        result.setResultWaitTime(DEFAULT_RESULT_WAIT_TIME);
        return result;
    }

}
