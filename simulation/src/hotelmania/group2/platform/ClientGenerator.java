package hotelmania.group2.platform;

import hotelmania.group2.dao.Client;
import hotelmania.group2.dao.Stay;

import java.util.Random;

public class ClientGenerator {

	private static Random randomNumber = new Random();

	public static Client randomClient()  {
		Client client = new Client();
		Stay stay = randomStay();
		client.setStay(stay);
		client.setBudget(randomBudget()*stay.getDays());
		return client;
	}

	public static double randomBudget() {
		return randomBetween(Constants.CLIENTS_BUDGET - Constants.CLIENTS_BUDGET_VARIANCE, 
							Constants.CLIENTS_BUDGET + Constants.CLIENTS_BUDGET_VARIANCE);
	}

	/**
	 * Generator of random real numbers
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static double randomBetween(double lower, double upper) {
		return randomNumber.nextDouble()*(upper-lower)+lower;
	}

	/**
	 * PRE: start+1 <= SIMULATION_DAYS
	 * @return
	 */
	public static Stay randomStay() {
		int start = randomBetween(1, Constants.SIMULATION_DAYS);
		int end = randomBetween(start+1, Constants.SIMULATION_DAYS);
		return new Stay(start, end);			
	}

	/**
	 * Generator of random integers
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static int randomBetween(int lower, int upper) {
		if (lower==upper) {
			return lower;
		}else if (lower<=0 || upper<=0) {
			return (int) randomBetween((double)lower, (double)upper);
		}
		return randomNumber.nextInt(upper-lower)+lower;
	}
}
