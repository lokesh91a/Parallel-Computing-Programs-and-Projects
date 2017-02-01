/**
 * 
 * @author Lokesh Agrawal
 * @version 4-NOV-2016
 *
 */
#define NT 1024

// Structure for a 1-D vector of points.
typedef struct
   {
   double x;
   double y;
   double z;
   }
   vector_points;

// Structure for a 1-D vector of result.   
typedef struct
   {
   int x;
   int y;
   double distance;
   }
   vector_result;

// Thread rank that found the best-of-best solution.
__device__ int bobRank;

// For shared memory reduction of threads for a particular pair.
__shared__ double shrRank[NT];

extern "C" __global__ void compute(vector_points *pointsList, 
vector_result *result, int N){
		
   	int k,oldRank, newRank, blockId;
	vector_points point1, point2;
	double distance1 = 0;
	double distance2 = 0;
	
	// Index of this thread within 
	int localThreadRank = threadIdx.x;
		
	  //blockId stores block id of a block in a gpu
	  blockId = blockIdx.x;
	  result[blockId].x = 0;
	  result[blockId].y = 0;
	  result[blockId].distance = 0;
	  point1 = pointsList[blockId];
   	  for (k = blockId+1; k < N; k++)
      {      
      		shrRank[localThreadRank] = 0;       	
      		point2 = pointsList[k];
      		
      		//Each thread calculates the distance of points for a particular pair
      		//of points.   		
      		for(int currentPoint=localThreadRank;currentPoint<N; currentPoint+=NT){
      			if(currentPoint==k || currentPoint==blockId)	continue;
      			distance1 = abs(pointsList[currentPoint].x-point1.x)+abs(pointsList[currentPoint].y-point1.y)+abs(pointsList[currentPoint].z-point1.z);
      			distance2 = abs(pointsList[currentPoint].x-point2.x)+abs(pointsList[currentPoint].y-point2.y)+abs(pointsList[currentPoint].z-point2.z);
      			
      			//The minimum distance out of two is added.
      			if(distance1<distance2)		shrRank[localThreadRank] = shrRank[localThreadRank]+distance1;
      			else	shrRank[localThreadRank] = 	shrRank[localThreadRank]+distance2;
     		}
  			
  			//Reduction of threads happens below to add distances of all threads
  			// and final result is stored at 0 index
  			__syncthreads();
    		 for (int p = NT/2; p > 0; p >>= 1)
      		{
      			if (localThreadRank < p && localThreadRank+p < NT){
         				shrRank[localThreadRank] = shrRank[localThreadRank] + shrRank[localThreadRank+p];
         		}
         		//Syncthread is must at this place to let the reduction of all
         		// threads complete
     			__syncthreads();
      		}		
  			
  			//if this block has no result till now then we copy the obtained
  			//result. Otherwise, result with min distance is copied.
  			if(result[blockId].distance==0){
  				result[blockId].distance = shrRank[0];
  				result[blockId].x = blockId;
  				result[blockId].y = k;
  			}
  			else if(shrRank[0]<result[blockId].distance){
  				result[blockId].distance = shrRank[0];
  				result[blockId].x = blockId;
  				result[blockId].y = k;
  			}
  				
      }

   // Global memory reduction to determine thread rank with best-of-best
   // solution across all blocks.
   if (localThreadRank == 0 && result[blockId].distance!=0)
      do
         {
         oldRank = bobRank;
         newRank =
            oldRank == -1 ||
            result[blockId].distance < result[oldRank].distance ?
               blockId : oldRank;
         }
      while (atomicCAS (&bobRank, oldRank, newRank) != oldRank);
   }