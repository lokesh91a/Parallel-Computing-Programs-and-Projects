/**
 *This class implements the functionality of calculating the power to a 
 *number.
 *@author Lokesh Agrawal id: la4401
 *Version: 28-SEP-2016
 */
public class CalculatePower {
	
	/**
	 * This function implements the logic for calculating the power of a paricular
	 * number in the long range
	 * @param number	long number of which power to be calculated
	 * @param power		power in int range
	 * @return	long	result is returned
	 */
	public static long powerFunction(long number, int power){
		int n=power;
		long result=1;
		while(n-->0)	result*=number;
		return result;
	}
}
