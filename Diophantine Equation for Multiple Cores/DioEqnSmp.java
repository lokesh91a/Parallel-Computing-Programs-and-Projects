/**
 * This class is the Parallel main class which implements the logic for
 * Diophantine equation. It is the parallel implementation of the logic and
 * extends pj2 task class.
 * 
 * author: Lokesh Agrawal id: la4401
 * version: 28-SEP-2016
 */
public class DioEqnSmp extends Task {
	// This stores the value for n in Diophantine equation
	int n = 0;

	// c stores the value of constant c in Diophantine equation
	// lowerLimit and upperLimit stores the values of the range of x,y and z
	// in Diophantine equation.
	long c = 0, lowerLimit = 0, upperLimit=0;

	// This are the global shared variables
	MinMaxVbl minVbl;
	MinMaxVbl maxVbl;
	LongVbl count;
	public void main(String[] args) throws Exception {
		//Catches all exceptions
		try {
			// If arguments passed is not equal to 4 then exception is thrown
			if (args.length != 4)
				throw new NumberFormatException();

			// Respective values of n,c, upperLimit and lowerLimit are taken
			// from
			// command-line arguments and this throws an exception if the
			// command line
			// arguments are not passed in the proper format
			n = Integer.parseInt(args[0]);
			if (n < 2)
				throw new NumberFormatException();
			c = Long.parseLong(args[1]);
			lowerLimit = Long.parseLong(args[2]);
			upperLimit = Long.parseLong(args[3]);

			// An object of global variables are created.
			maxVbl = new MinMaxVbl.MaxVbl(new MinMax(Long.MIN_VALUE));
			minVbl = new MinMaxVbl.MinVbl(new MinMax(Long.MAX_VALUE));
			count = new LongVbl.Sum(0);
			
			// This is the parallelFor loop which runs all the tasks in parallel
			// inside the loop and I have used dynamic type scheduling with a chunk
			//size of 100
			parallelFor(lowerLimit, upperLimit).schedule(dynamic).
			chunk(100).exec(new LongLoop() {
				
				//Thread local variables have been defined
				MinMaxVbl minLocalVbl;
				MinMaxVbl maxLocalVbl;
				LongVbl countF;
				// Vbl is initialized inside start() method so that for each
				// thread, a new instance of vbl is created.
				public void start() {
					minLocalVbl = threadLocal(minVbl);
					maxLocalVbl = threadLocal(maxVbl);
					countF = (LongVbl)threadLocal(count);
				}

				//These variables are used to store calculation of power of x and
				//y to reduce the computation.
				long xPower=0,yPower=0;
				// This is the run method of parallelfor Loop in which all the
				// tasks run in parallel and x is the value for each iteration
				public void run(long x) {
					xPower=CalculatePower.powerFunction(x, n);
					// for loop for iterating on all y values
					for (long y = x; y <= upperLimit; y++) {
						yPower=CalculatePower.powerFunction(y, n);
						// for loop for iterating on all z values
						for (long z = lowerLimit; z <= upperLimit; z++) {
							// if check method returns true then increment
							// method is
							// called which increases the total count
							if(xPower+yPower == CalculatePower.powerFunction(z, n) + c) {
								++countF.item;
								// if this is the first solution found then
								// minimum solution is updated by calling 
								//set method.
								if (countF.longValue() == 1) {
									minLocalVbl.minMax.setX(x);
									minLocalVbl.minMax.setY(y);
									minLocalVbl.minMax.setZ(z);
								}
								//Max solution is always updated whenever a solution is found
									maxLocalVbl.minMax.setX(x);
									maxLocalVbl.minMax.setY(y);
									maxLocalVbl.minMax.setZ(z);
							}
						}
					}

				}
			});

			// if total number of solutions found is equal to 0 then just 0 is
			// printed
			if (count.longValue() == 0)
				System.out.println(count.longValue());
			// if only 1 solution is found then count and the solution found is
			// printed
			else if (count.longValue() == 1) {
				System.out.println(count.longValue());
				System.out.println(minVbl.minMax.getX() + "^" + n + " + " + minVbl.minMax.getY() + "^" + n + " = "
						+ minVbl.minMax.getZ() + "^" + n + " + " + c);
			}
			// if more than 1 solution is found then the number of solutions
			// found,
			// minimum solution and maximum solution is printed.
			else {
				System.out.println(count.item);
				System.out.println(minVbl.minMax.getX() + "^" + n + " + " + minVbl.minMax.getY() + "^" + n + " = "
						+ minVbl.minMax.getZ() + "^" + n + " + " + c);
				System.out.println(maxVbl.minMax.getX() + "^" + n + " + " + maxVbl.minMax.getY() + "^" + n + " = "
						+ maxVbl.minMax.getZ() + "^" + n + " + " + c);
			}
		} catch (Exception e) {
			System.out.println("Number format exception: Kindly give command"
					+ " line arguments in proper format and n must be greater than or equal to" + " 2");
		}

	}

}
