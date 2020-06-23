package se.pensionsmyndigheten.icc.test.orderprocessor.strategy;

import static org.junit.Assert.*;

import org.junit.Test;

public class OrderAggregatingStrategyTest {

	private static final String ORDER_1 = "<order>1</order>";
	private static final String ORDER_2 = "<order>2</order>";

//	@Test
	public void testAppendOrder() {
		OrderAggregatingStrategy strategy = new OrderAggregatingStrategy();
		String orders = strategy.appendOrder("<orders></orders>", ORDER_1);
		assertTrue(orders.equals("<orders>" + ORDER_1 + "</orders>"));
	}

}
