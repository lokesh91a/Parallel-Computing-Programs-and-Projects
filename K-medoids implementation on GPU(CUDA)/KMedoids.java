import java.nio.ByteBuffer;

import edu.rit.gpu.Gpu;
import edu.rit.gpu.GpuIntVbl;
import edu.rit.gpu.GpuStructArray;
import edu.rit.gpu.Kernel;
import edu.rit.gpu.Module;
import edu.rit.gpu.Struct;
import edu.rit.pj2.Task;
import edu.rit.util.Instance;


/**
 * 
 * @author Lokesh Agrawal
 * @version 2-NOV-2016
 *
 */
public class KMedoids extends Task{
	
	//Structure for a storing points.
	private static class VectorPoints extends Struct{
		public double x;
		public double y;
		public double z;
		
		//Constructor for a new structure of points
		public VectorPoints(double x, double y, double z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		//Returns the size in bytes of the C struct.
		public static long sizeof(){
			return 24;
		}
		
		 //This Java object is used to give byte buffer as a C struct.
		public void toStruct(ByteBuffer buf){
			buf.putDouble(x);
			buf.putDouble(y);
			buf.putDouble(z);
		}
		
		//This Java object is read from the given byte buffer as a C struct.
		public void fromStruct(ByteBuffer buf){
			this.x = buf.getDouble();
			this.y = buf.getDouble();
			this.z = buf.getDouble();
		}
	}
	
	//Structure for a storing result which has 3 fields: index1, index2 and 
	//minimum distance.
	private static class Result extends Struct{
		public int index1;
		public int index2;
		public double distance;
		
		//Constructor for a new structure of results
		public Result(int index1, int index2, double dist){
			this.index1 = index1;
			this.index2 = index2;
			this.distance = dist;
		}
		
		// Returns the size in bytes of the C struct.
		public static long sizeof(){
			return 16;
		}
		
		//This Java object is used to give byte buffer as a C struct.
		public void toStruct(ByteBuffer buf){
			buf.putInt(index1);
			buf.putInt(index2);
			buf.putDouble(distance);
		}
		
		//This Java object is read from the given byte buffer as a C struct.
		public void fromStruct(ByteBuffer buf){
			this.index1 = buf.getInt();
			this.index2 = buf.getInt();
			this.distance = buf.getDouble();
		}
	}
	
	//List of points.
	GpuStructArray<VectorPoints> gpuPointsList;
	//List of results
	GpuStructArray<Result> resultList; 
	
	/**
	* Kernel function interface.
	*/
	public static interface KMedoidsKernel extends Kernel{
		public void compute(GpuStructArray<VectorPoints> gpuPointsList, GpuStructArray<Result> resultList, int dist);
	}
	
	/**
	* Task main program.
	*/
	public void main(String args[]) throws Exception{
		//If number of arguments is less than 1 then this throws an error
		if(args.length!=1)	System.out.println("Kindly enter correct input" +
				" in the format of 'RandomPointGroup(20,10,314160)'");
		
		//Instance of PointGroup is stored in an object of PointGroup interface.
		PointGroup pg = (PointGroup) Instance.newInstance (args[0]);
		
		//Total number of points are captured.
		int numberOfPoints = pg.N();
		
		//Object of Point class to store points
		Point point = new Point();
		
		// Initialize GPU.
		Gpu gpu = Gpu.gpu();
		gpu.ensureComputeCapability(2, 0);
		
		
		// Set up GPU variables.
		Module module = gpu.getModule("KMedoids.ptx");
		
	     int numberOfThreads = 1024;
	     int gridSize = numberOfPoints;
		
		GpuIntVbl finalResultRank = module.getIntVbl ("bobRank");
        finalResultRank.item = -1;
        finalResultRank.hostToDev();
        
        gpuPointsList =
           gpu.getStructArray (VectorPoints.class, numberOfPoints);
        for (int i = 0; i < numberOfPoints; ++ i){
        	pg.nextPoint(point);
        	gpuPointsList.item[i] = new VectorPoints(point.x, point.y, point.z);
        }
           
        
        gpuPointsList.hostToDev();		              
		
        resultList =
                gpu.getStructArray (Result.class, gridSize, 1);
             resultList.item[0] = new Result(0,0,0);             
        
		// Set up GPU kernel.
		KMedoidsKernel kernel = module.getKernel(KMedoidsKernel.class);				
		
		//Grid dimension is set to number of points
		kernel.setGridDim (gridSize);
		kernel.setBlockDim(numberOfThreads);
		
		kernel.compute(gpuPointsList, resultList,numberOfPoints);
        // Get best solution from GPU.
        finalResultRank.devToHost();
        resultList.devToHost (0, finalResultRank.item, 1);
		System.out.println(resultList.item[0].index1);
		System.out.println(resultList.item[0].index2);
		System.out.printf("%.3f", resultList.item[0].distance);
		System.out.println();
	}
	
	/**
	*This specifies that task requires one core.
	*/
	protected static int coresRequired(){
		return 1;
	}
	
	/**
	*This specifies task requires one GPU accelerator.
	*/
	protected static int gpusRequired(){
		return 1;
	}
}
