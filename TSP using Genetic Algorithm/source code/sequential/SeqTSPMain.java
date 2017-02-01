
import java.io.IOException;
import java.util.ArrayList;

import edu.rit.io.InStream;
import edu.rit.io.OutStream;
import edu.rit.pj2.Job;
import edu.rit.pj2.Task;
import edu.rit.pj2.Tuple;
import edu.rit.pj2.tuple.ObjectTuple;
import edu.rit.util.Instance;

/**
 * Class SeqTSPMain is a sequential program to solve traveling sales man problem
 * 
 * <P>
 * Usage: java pj2 jar={@literal <jarfile>} workers={@literal <k>} seqTSPMain {@literal "<ctor>" <populationsize> <GAiterations>} <br>
 * {@literal <jarfile>} = Name of the java archieve file containing all the java class files. <br>
 * {@literal <k>} = The number of worker tasks. <br>
 * {@literal <ctor>} = Constructor expression of the input graph.
 * {@literal <populationsize>} = The size of the population
 * {@literal <GAiterations>} = The iterations required for genetic algorithm to run
 * 
 * 
 * @author Sahil Jasrotia, Lokesh Agrawal
 * @version 08-Dec-2016
 * 
 */
public class SeqTSPMain extends Job {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_GREEN = "\u001B[32m";	
	public static final String ANSI_CYAN  = "\u001B[36m";
	private TravelingPath initTour;		// To store the initial tour
	PointGroup pg;						// Reference to point group 
	private int numCities;				// To store number of cities
	
	/**
	 * Main program.
	 * 
	 * @param args Array of zero or more command line argument strings.
	 * @throws Exception The main method can throw any exception.	 
	 */
	public void main(String[] args) throws Exception {
		try{
			// Raise error if insufficient arguments.
			if(args.length != 3)
				usage();

			// verify parameters
			checkParameters(args);			
			
			// Create initial tour
			createTravellingPath(args[0]);				
			
			// put the initial tour into the tuple space
			putTuple( new ObjectTuple<TravelingPath>(initTour) );
			
			// Set up a task group of K worker tasks
			rule().task(workers(), WorkerTask.class).args(args);
			
			// Set up reduction task
			rule().atFinish().task(ReduceTask.class).runInJobProcess().args();
		}
		catch(Exception e){
			usage();
		}
	}
	
	/**
	 * Print a usage message and exit.
	 */
	private static void usage() {
		System.err.println("Usage: java pj2 jar=<jarfile> workers=<k> seqTSPMain <ctor> <populationsize> <GAiterations>");
		System.err.println("<jar> Name of the java archieve file containing all the java class files. ");
		System.err.println("<workers> Number of workers.");
		System.err.println("<ctor> The constructor expression. The first argument to constructor expression is"
							+ "Number of cities.");
		System.err.println("<populationsize> The size of the population.");
		System.err.println("<GAiterations> Number of genetic algorithm iterations.");		
		terminate(1);
	}
	
	/**
	 * Check if the arguments 1 2 and 3 are integers.
	 * This method will throw an exception if the arguments are not integers.
	 * The exception will be captured by the main program which will exit the program.
	 * 
	 * @param args Command line arguments.
	 */
	private static void checkParameters(String args[]){
		Integer.parseInt(args[1]);
		Integer.parseInt(args[2]);		
	}
	
	/**
	 * This method creates initial tour for the genetic algorithm
	 * 
	 * @param pointGroup Contains constructor for creating pointgroup instance
	 * @throws Exception Instance class throws the exception
	 */
	public void createTravellingPath(String cityGroup) throws Exception{	
		// create an instance of the point group
		this.pg = (PointGroup) Instance.newInstance (cityGroup);
		
		// Get the number of cities that user has entered
    	numCities = pg.N();
    	
    	// id used as city identifier
    	int id = 0;
    	
    	// Initial tour list
    	ArrayList<City> initTour = new ArrayList<City>();
    	City city = new City();    	
    	
    	// Get the points from the point group class and put it in citylist array
		for (int i = 0; i < numCities;  i++){
			pg.nextPoint(city);
			initTour.add(new City(city.x,city.y,id++));			
		}		
		// initial tour initialization
		this.initTour = new TravelingPath(initTour);											
	}
	
	/**
	 * Class WorkerTask runs the genetic algorithm for the given number of iterations
	 * 
	 * @author Sahil Jasrotia, Lokesh Agrawal
	 * 
	 */
	private static class WorkerTask extends Task {

		private TravelingPath initTour;			// To store the initial tour.
		private int popSize;					// Holds the population size.
		private int iterGA;						// Number of iteration for GA.
		private Population population;			// population reference.
		
		/**
		 * Worker task main program
		 */
		public void main(String[] args) throws Exception {
			
			// read the tours 
			initTour = readTuple(new ObjectTuple<TravelingPath>()).item; 
			
			// number of generations for genetic algorithm 
			iterGA = Integer.parseInt(args[2]);			
			
			popSize = Integer.parseInt(args[1]);					
			
			// initialize genetic algorithm
			population = new Population (initTour,popSize);
			
			population.createPopulation();
			
			// run genetic algorithm
			population.startGA(iterGA);
			
			// Sort the population so that we have best population at the top of the list.
			population.sortPopultaion();
			
			// The best result is sent in tuple space
			TravelingPath bestTour = population.getPopulationList().get(0);			
			putTuple( new ResultTuple(bestTour));
			
		}
	}
	
	/**
	 * Class ResultTuple Contains the intermediate and final results after reduction. 
	 * 
	 * @author Sahil Jasrotia, Lokesh Agrawal
	 * 
	 */
	private static class ResultTuple extends Tuple {
		
		public TravelingPath bestTour;		// To store the optimal tour.
		
		/**
		 * Default constructor to create bestTour
		 * 
		 */
		public ResultTuple() {
		
		}
		
		/**
		 * Parameterized constructor to create bestTour
		 * 
		 * @param bestTour tour that is best of all tours 		 
		 */
		public ResultTuple( TravelingPath bestTour) {
			this.bestTour = bestTour;			
		}

		/**
		 * Read the fields of this tuple from the given input stream
		 * 
		 * @param in The input stream
		 * 
		 * @exception  IOException Throws the IO exception if there is an error
		 */
		public void readIn(InStream inStream) throws IOException {
			bestTour = (TravelingPath) inStream.readObject();			
		}

		/**
		 * Write the fields of this tuple to the out stream
		 * 
		 * @param out The output stream
		 * 
		 * @exception  IOException Throws the IO exception if there is an error
		 */
		public void writeOut(OutStream outStream) throws IOException {
			outStream.writeObject(bestTour);			
		}
		
		/**
		 * Print the result 
		 * 		 
		 */
		public void printResults() {					
			
			// Get the citylist from the best tour we have
			ArrayList<City> cityList  = bestTour.getCityList();			
			
			// Print the city id to identify city in a tour.
			for( int i = 0; i < cityList.size(); i++ ) {
				System.out.print(ANSI_CYAN + cityList.get(i).id + ANSI_RESET);
				System.out.print(ANSI_CYAN + "-->" + ANSI_RESET);
			}
			System.out.println(ANSI_CYAN + cityList.get(0).id +"\n"+ ANSI_RESET);
			
			// Print the optimal distance.
			System.out.println(ANSI_GREEN + "OPTIMAL DISTANCE: " + ANSI_RESET);
			System.out.printf(ANSI_CYAN);
			System.out.printf ("%.3f", bestTour.getEuclideanDistance());
			System.out.printf ("\n" + ANSI_RESET);
			
		}		
	}
	
	/**
	 * The ReduceTask class is used to get results from all the nodes and then reduces
	 * the result to get the final result.  
	 * 
	 * @author Lokesh Agrawal, Sahil Jasrotia
	 * 
	 */
	private static class ReduceTask extends Task { 
		
		/**
		 * Reduce task main program.
		 */
		public void main(String[] args) throws Exception {
						
			ResultTuple template = new ResultTuple();	 // Template for result tuple 
			ResultTuple resultTuple = new ResultTuple(); // result tuple object
			ResultTuple result = null;
			
			// Try to take result tuple from the tuple space
			while( ( result = tryToTakeTuple(template) ) != null )
			{
				// Get the result and assign it to result tuple object
				resultTuple = result;
			}
			
			// display result
			System.out.println(ANSI_GREEN + "OPTIMAL PATH: " + ANSI_RESET);
			resultTuple.printResults();			
		}		
	}
	
	/**
	 * Specifies the number of cores required for this program
	 * 
	 * @return the number of cores
	 */
	protected static int coresRequired() {
		return 1;
    }
}