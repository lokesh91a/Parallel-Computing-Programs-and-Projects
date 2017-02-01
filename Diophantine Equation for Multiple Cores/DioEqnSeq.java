import edu.rit.pj2.Task;

/**
 * This class is the Sequential main class which implements the logic for
 * Diophantine equation. It is the sequential implementation of the logic and
 * extends pj2 task class.
 * 
 * author: Lokesh Agrawal id: la4401
 * Version: 28-SEP-2016
 */
public class DioEqnSeq extends Task {
	/**
	 * This is the main method which executes when program executes this file.
	 * It takes 4 commamd line arguments as input each of which must be an
	 * integer within the range of long except the 1st argument which must be an
	 * integer in the range of int
	 */
	public void main(String[] args) throws Exception {
		//Catches all exceptions
		try {
			// If length of commandline arguments passed are not equal to 4 then
			// exception is thrown
			if (args.length != 4)
				throw new NumberFormatException();

			// Variables for storing command line argument passed have been
			// defined
			// and initialized
			int n = 0;
			long c = 0, lowerLimit = 0, upperLimit = 0;

			// variables of long type to store the min and max result
			long count = 0;
			
			//Two objects of MinMax class are created to store the solutions
			//og minimum and maximum
			MinMax minSol = new MinMax(Long.MAX_VALUE);
			MinMax maxSol = new MinMax(Long.MIN_VALUE);

			// All comman-line arguments are collected into the respective
			// variables
			n = Integer.parseInt(args[0]);
			if (n < 2)
				throw new NumberFormatException();
			c = Long.parseLong(args[1]);
			lowerLimit = Long.parseLong(args[2]);
			upperLimit = Long.parseLong(args[3]);

			//This is used to store the calculation of power of x and Y for
			//each loop on z
			long xPower=0,yPower=0;
			// 3 for loops to iterate on each combination of x, y and z in
			// sequential order
			for (long x = lowerLimit; x <= upperLimit; x++) {
				xPower=CalculatePower.powerFunction(x, n);
				for (long y = x; y <= upperLimit; y++) {
					yPower=CalculatePower.powerFunction(y, n);
					for (long z = lowerLimit; z <= upperLimit; z++) {
						// Check method is called to check if the respective
						// values
						// passed satisfies the Diophantine equation
						if (xPower+yPower== CalculatePower.powerFunction(z, n) + c) {
							// If true is returned then count is increased
							count++;
							// if count is 1 the minimum solution variables are
							// updated
							if (count == 1) {
								minSol.setX(x);
								minSol.setY(y);
								minSol.setZ(z);
							}
							// always maximum solution variables are
							// updated
								maxSol.setX(x);
								maxSol.setY(y);
								maxSol.setZ(z);
						}
					}
				}
			}
			// if total number of solutions found is equal to 0 then just 0 is
			// printed
			if (count == 0)
				System.out.println(count);

			// if only 1 solution is found then count and the solution found is
			// printed
			else if (count == 1) {
				System.out.println(count);
				System.out.println(minSol.getX() + "^" + n + " + " + minSol.getY() + "^" + n + " = " + minSol.getZ() + "^" + n + " + " + c);
			}

			// if more than 1 solution is found then the number of solutions
			// found,
			// minimum solution and maximum solution is printed.
			else {
				System.out.println(count);
				System.out.println(minSol.getX() + "^" + n + " + " + minSol.getY() + "^" + n + " = " + minSol.getZ() + "^" + n + " + " + c);
				System.out.println(maxSol.getX() + "^" + n + " + " + maxSol.getY() + "^" + n + " = " + maxSol.getZ() + "^" + n + " + " + c);
			}
		} catch (Exception e) {
			System.out.println("Number format exception: Kindly give command"
					+ " line arguments in proper format and n must be greater than or equal to" + " 2");
		}
	}
	
	/**
	 * Specify that this task requires one core.
	 * @return int no. of cores
	 */
	public static int coresRequired(){
		return 1;
	}
}
