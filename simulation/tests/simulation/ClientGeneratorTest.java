package simulation;

import hotelmania.group2.dao.Client;
import hotelmania.group2.dao.Stay;
import hotelmania.group2.platform.ClientGenerator;
import hotelmania.group2.platform.Constants;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClientGeneratorTest {
	
	@Before
	public void setUp() throws Exception {
		Constants.SIMULATION_DAYS = 10;
		Constants.CLIENTS_BUDGET = 10;
		Constants.CLIENTS_BUDGET_VARIANCE = 1;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRandomClient() {
		for (int i = 0; i < 100; i++) {
			Client c = ClientGenerator.randomClient();
			double oneDayBudget = c.getBudget()/c.getStay().getDays();
			Assert.assertEquals(oneDayBudget , Constants.CLIENTS_BUDGET, Constants.CLIENTS_BUDGET_VARIANCE) ;
		}

	}

	@Test
	public void testRandomBudget() {
		for (int i = 0; i < 100; i++) {
			double oneDayBudget = ClientGenerator.randomBudget();
			Assert.assertEquals(oneDayBudget , Constants.CLIENTS_BUDGET, Constants.CLIENTS_BUDGET_VARIANCE) ;
		}
	}

	@Test
	public void testRandomBetweenDouble() {
		for (int i = 0; i < 100 ; i++) {
			Assert.assertTrue(ClientGenerator.randomBetween(1.0, 2.0)<=2);

			Assert.assertEquals(ClientGenerator.randomBetween(100.0, 100.0), 100.0, 1e-10);

			Assert.assertTrue(ClientGenerator.randomBetween(-10.0, 1.0) <= 1.0);

			Assert.assertTrue(ClientGenerator.randomBetween(0, 1.0)<=1.0);
		} 
	}

	@Test
	public void testRandomStay() {
		for (int i = 0; i < 100 ; i++) {
			Stay stay = ClientGenerator.randomStay();
			Assert.assertTrue(stay.getDays() <= Constants.SIMULATION_DAYS);
			Assert.assertTrue(stay.getCheckIn() <= Constants.SIMULATION_DAYS);
			Assert.assertTrue(stay.getCheckOut() <= Constants.SIMULATION_DAYS);
			Assert.assertTrue(stay.getCheckIn() < stay.getCheckOut() );
			
		}
		
	}

	@Test
	public void testRandomBetweenInt() {
		for (int i = 0; i < 100 ; i++) {

			Assert.assertTrue(ClientGenerator.randomBetween(1, 2)<=2);

			Assert.assertEquals(ClientGenerator.randomBetween(100, 100), 100);

			Assert.assertTrue(ClientGenerator.randomBetween(-10, 1) <= 1);

			Assert.assertTrue(ClientGenerator.randomBetween(0, 1)<=1);
		}
	}
}
