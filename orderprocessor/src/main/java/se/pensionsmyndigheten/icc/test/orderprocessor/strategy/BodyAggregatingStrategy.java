package se.pensionsmyndigheten.icc.test.orderprocessor.strategy;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class BodyAggregatingStrategy implements AggregationStrategy {

	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange != null) {
			String oldBody = oldExchange.getIn().getBody(String.class);
			String newBody = newExchange.getIn().getBody(String.class);
			newExchange.getMessage().setBody(oldBody + newBody);
		}
		return newExchange;
	}

}
