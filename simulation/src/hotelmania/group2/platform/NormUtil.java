package hotelmania.group2.platform;

public class NormUtil {
	private static final double NORMALIZED_HIGH = 1;
	private static final double NORMALIZED_LOW = 0;

	/**
	 * Normalize x.
	 * @param x The value to be normalized.
	 * @return The result of the normalization.
	 */
	public static double normalize(double x, double dataLow, double dataHigh) {
		return ((x - dataLow) 
				/ (dataHigh - dataLow))
				* (NORMALIZED_HIGH - NORMALIZED_LOW) + NORMALIZED_LOW;
	}
 
	public static void main(String[] args) {
		double start = 7;
		double x = normalize(start, 0, 10);
		System.out.println(start + " normalized is " + x);
	}
	
}