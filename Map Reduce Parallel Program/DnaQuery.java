import edu.rit.pjmr.Combiner;
import edu.rit.pjmr.Customizer;
import edu.rit.pjmr.Mapper;
import edu.rit.pjmr.PjmrJob;
import edu.rit.pjmr.Reducer;
import edu.rit.pjmr.TextDirectorySource;
import edu.rit.pjmr.TextId;

/**
 * The DnaQuery class finds the best score at the least index for all the
 * keys in an file. This program runs in parallel in an cluster
 * 
 * @author Lokesh Agrawal
 * @version 1-DEC-2016
 */
public class DnaQuery extends PjmrJob<TextId, String, String, Solution> {

	/**
	 * PJMR job main program.
	 * 
	 * @param args	Command line arguments.
	 */
	public void main(String[] args) {
		// Parsing command line arguments. If command-line arguments
		//are not equal to 4 then error is thrown
		if (args.length != 4)
			usage();
		
		String[] names = args[0].split(",");
		
		//Directory path is stored
		String directory = args[1];

		// Determine number of mapper threads.
		int NT = Math.max(threads(), 1);
		System.out.flush();

		// Configure mapper tasks.
		for (String name : names)
			mapperTask(name).source(new TextDirectorySource(directory)).mapper(
					NT, MyMapper.class,args);

		// Configure reducer task.
		reducerTask().customizer(MyCustomizer.class).reducer(MyReducer.class);

		startJob();
	}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage() {
		System.err
				.println("Some error. Kindly rectify and run again");
		terminate(1);
	}

	/**
	 * Mapper class. This class contains logic for finding the solutions
	 * and feeding the solutions to the combiner which automatically does 
	 * reduction.
	 */
	private static class MyMapper extends
			Mapper<TextId, String, String, Solution> {
		//Solution Vbl field
		private Solution localSolution;
		
		//For storing key and input. StringBuilder is used as they are fast
		//as compared to Strings.
		private StringBuilder key, input;
		private String query;
		private double threshold, leastValue;

		/**
		 * Start method is called only once while starting MyMapper.
		 * In this all the values have been initialized.
		 */
		public void start(String[] args, Combiner<String, Solution> combiner) {
			localSolution = new Solution(-1,0);
			input = new StringBuilder();
			key = new StringBuilder();
			query = args[2];
			threshold = Double.parseDouble(args[3]);
		}

		/**
		 * This method gets called again and again passing each line in 
		 * contents.
		 * @param contents next line in program
		 * 		  combiner combiner is passed
		 */
		public void map(TextId id,
				String contents,
				Combiner<String, Solution> combiner) {
			//If '>' sign is found then we have found the starting of the key
			if (contents.charAt(0) == '>') {
				//If we already have key, then evaluate the solution for 
				//previous key
				if (key.length() != 0) {
					findSolution(combiner);
					//key gets cleared for new key
					key = new StringBuilder();
					//input gets cleared for new input
					input = new StringBuilder();
					
				}
				//else key is made
				int i = 1;
				while (i < contents.length()
						&& !Character.isWhitespace(contents.charAt(i)))
					key.append(contents.charAt(i++));
			}
			
			//If 1st index of line is not ">" then content is appended to 
			//the input
			else
				input.append(contents);
		}

		/**
		 * This method is called at the end after doing all processing so 
		 * that processing for last key can be done
		 */
		public void finish(Combiner<String, Solution> combiner) {
			findSolution(combiner);
		}
		
		/**
		 * This function is the actual logic for calculating score at each index
		 * and then feeding it to the combiner.
		 * @param combiner
		 */
		public void findSolution(Combiner<String, Solution> combiner){
			long score = 0;
			long index = -1;
			
			//Threshold is calculated
			leastValue = threshold*(double)query.length();
			
			//Two loops are used to find the score at each index
			for (int i = 0; i < input.length(); i++) {
				score = 0;
				index = i;
				int j = i;
				int counter = 0;
				//Inner loop for find score at particular index
				while (j < input.length() && counter < query.length()) {
					if (input.charAt(j) == query.charAt(counter))
						score++;
					j++;
					counter++;
				}
				
				//New instance of local solution is created
				localSolution = new Solution(index, score);
				//If score satisfies the given condition then added to combiner
				if (score >= leastValue) {
					combiner.add(key.toString(), localSolution);
				}
			}
		}
	}

	/**
	 * Reducer task customizer class. 
	 * This class is used to sort the output
	 */
	private static class MyCustomizer extends Customizer<String, Solution> {
		public boolean comesBefore(String key_1, Solution solution_1,
				String key_2, Solution solution_2) {
			if ((solution_1.score == solution_2.score && key_1.compareTo(key_2)<0)
					|| (solution_1.score > solution_2.score))
				return true;
			else
				return false;
		}
	}

	/**
	 * Reducer class. This class just prints the output in the end
	 */
	private static class MyReducer extends Reducer<String, Solution> {
		public void reduce(String key, Solution value) {
			System.out.print(value.score+"\t"+value.index+"\t"+key);
			System.out.println();
		}
	}

}