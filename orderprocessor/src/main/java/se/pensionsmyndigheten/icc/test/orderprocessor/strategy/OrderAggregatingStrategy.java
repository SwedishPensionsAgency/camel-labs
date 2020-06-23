package se.pensionsmyndigheten.icc.test.orderprocessor.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class OrderAggregatingStrategy implements AggregationStrategy {

	private final static String ORDER_ROOT_START = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
			"<orders xmlns=\"http://test.icc.pensionsmyndigheten.se/order\">\n\t";
	private final static String ORDER_ROOT_END = "</orders>";
	
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange == null) {
			String orders = newExchange.getIn().getBody(String.class);
			newExchange.getMessage().setBody(wrapWithRootElement(orders));
		}
		else
		{
			String currentOrders = oldExchange.getIn().getBody(String.class);
			String newOrder = newExchange.getIn().getBody(String.class);
			newExchange.getMessage().setBody(appendOrder(currentOrders, newOrder));
		}
		return newExchange;
	}

	protected String wrapWithRootElement(String order) {
		String newBodyString = ORDER_ROOT_START;
		newBodyString += order;
		newBodyString += ORDER_ROOT_END;
		return newBodyString;
	}

	protected String appendOrder(String oldBody, String newOrder) {
		StringBuffer oldBodyStringBuffer = new StringBuffer(oldBody);
		int endTag = oldBody.lastIndexOf("</");
		oldBodyStringBuffer.insert(endTag, "\t" + newOrder + "\n");
		return oldBodyStringBuffer.toString();
	}
}
