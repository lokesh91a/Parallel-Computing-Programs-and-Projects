import java.lang.reflect.InvocationTargetException;
import edu.rit.pj2.Job;
import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.pj2.tuple.ObjectArrayTuple;
import edu.rit.pj2.vbl.LongVbl;
import edu.rit.util.BitSet;
import edu.rit.util.Instance;

/**
 * Class ClusterCoeff implements cluster parallel program to calculate cluster
 * coefficient.
 * 
 * @author Lokesh Agrawal
 * @version 12-OCT-2016
 */
public class ClusterCoeff extends Job {

	/**
	 * Job main program.
	 */
	public void main(String[] args) throws ClassNotFoundException, 
	NoSuchMethodException, InstantiationException,
	IllegalAccessException, InvocationTargetException {
		
		Graph input = null;
		try{
			if(args.length!=1)	throw new Exception();
			//parse command line arguments
			input = (Graph) Instance.newInstance(args[0]);
		}
		catch(Exception e){
			System.out.println("Kindly ensure that there is only 1 command "
					+ "line argument and is in the proper format which is"
					+ "as follows: RandomGraph(vertices,edges,seed)");
		}
		//Takes number of vertices for master Loop
		int v = input.V();
		
		//Proportional scheduling with 10 chunk size is used.
		masterSchedule(proportional);
		masterChunk(10);
		
		// Set up a task group of K worker tasks.
		masterFor(0, v - 3, WorkerTask.class).args(args[0]);
		
		//// Set up reduction task at finish rule.
		rule().atFinish().task(ReduceTask.class).runInJobProcess();
	}
	
	/**
	 * Class ClusterCoeff.WorkerTask provides a task that computes chunks of
	 * iterations in the Cluster Coefficient computation.
	 *
	 * @author  Lokesh Agrawal
	 * @version 12-OCT-2016
	 */
	private static class WorkerTask extends Task {
		//For storing graph
		BitSet[] graph;
		
		//Object of Solution
		Solution output;

		public void main(String[] arg0) throws Exception {
			
			//Reads graph and stores it in BitSet by iterating on all edges
			Graph input = (Graph) Instance.newInstance(arg0[0]);
			final int v = input.V();
			final int e = input.E();
			graph = new BitSet[v];
			for (int i = 0; i < v; i++)
				graph[i] = new BitSet(v);
			Edge edge = new Edge();
			for (int i = 0; i < e; i++) {
				input.nextEdge(edge);
				graph[edge.v1].add(edge.v2);
				graph[edge.v2].add(edge.v1);
			}
			
			//Object of Solution is initialized
			output = new Solution(0);
			
			//Worker for loop distributed load on each core within each node
			workerFor().schedule(dynamic).exec(new Loop() {	
				
				//Thread local solution
				Solution outputLocal;
				boolean result1 = false;
				public void start() {
					//Threadlocal solution defined.
					outputLocal = threadLocal(output);
				}
				
				@Override
				/**
				 * This method contains all inner loops and logic of computing
				 * number of triangles and number of triplets on each node.
				 * 
				 * @param	sidei	Iterating value from outer loop.
				 */
				public void run(int sidei) {
					//Loop 2 with range i+1 to v-2
					for (int sidej = sidei + 1; sidej < v - 1; sidej++) {
						//side1 existence result is stored in boolean
						result1 = graph[sidei].contains(sidej);
						//Loop 3 with range j+1 to v-1
						for (int sidek = sidej + 1; sidek < v; sidek++) {
							//Most optimum logic for calculating triangles and
							//triplets. Faster than computing all sides just 
							//once each time.
							if(result1){
								if(graph[sidej].contains(sidek)){
									if(graph[sidei].contains(sidek)){
										outputLocal.triangle++;
									}
									else{
										outputLocal.triplet++;
									}
								}
								else{
									if(graph[sidei].contains(sidek))
										outputLocal.triplet++;
								}
							}
							else{
								if(graph[sidej].contains(sidek) &&
							 graph[sidei].contains(sidek))
									outputLocal.triplet++;
							}
						}
					}
					
				}
			});
			//output is put into tuple space for reduction to take place.
			putTuple(output);
		}
	}

	/**
	 * Class ClusterCoeff.ReduceTask combines the worker tasks results and prints
	 * the overall result.
	 *
	 * @author  Lokesh Agrawal
	 * @version 12-OCT-2016
	 */
	private static class ReduceTask extends Task {
		//Reduce task main program.
		public void main(String[] arg0) throws Exception {
			//Final solution is stored in this count.
			Solution count = new Solution(0L);
			
			//template is used to identify this type of tuple in tuple space.
			Solution template = new Solution();
			
			//When a template is identified then it is stored in taskCount.
			Solution taskCount;
			while ((taskCount = tryToTakeTuple(template)) != null)
				//Reduce menthod of Solution class is called for reduction.
				count.reduce(taskCount);
			
			//Final results are printed.
			System.out.println(count.triangleValue());
			System.out.println(count.tripletValue()+(3*count.triangleValue()));
			//Calculates Cluster Coefficient
			double result = 3*((double)count.triangleValue()/
					(double)(count.tripletValue()+3*count.triangleValue()));
			//Prints result upto 5 decimal places
			System.out.printf("%.5f", result);
			System.out.println();
		}
	}
}