import edu.rit.pj2.Vbl;
/**
 * MinMaxVbl is a class which implements the interface Vbl. Objects of this
 * class can be used as a shared variables in parallel programming.
 * @author Lokesh Agrawal id: la4401
 *
 */
public class MinMaxVbl implements Vbl{
	
	//minMax is the object of the class MinMax
	public MinMax minMax;
	
	/**
	 * This is the parameterized constructor which states that while 
	 * initializing the object of the class MinMaxVbl, object of class
	 * MinMax has to be passed.
	 * 
	 * @param val	Object of class MinMax
	 */
	public MinMaxVbl(MinMax val){
		this.minMax = val;
	}
	
	/**
	 * Each thread creates its own copy of the shared variable by calling
	 * this clone() method on the given loop object.
	 */
	@Override
	public Object clone(){
		MinMaxVbl vbl = null;
		try {
			vbl = (MinMaxVbl)super.clone();
			if(this.minMax!=null)	vbl.minMax = (MinMax) this.minMax.clone();
			return vbl;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Shared variable not created "
					+ "properly", e);
		}
	}
	
	/**
	 * This method is called only once and by default. When all the reduction 
	 * has already happened and now the final result needs to be copied into 
	 * global variable.
	 * 
	 * @param val Always an object of interface Vbl is passed
	 * 
	 * @return	None
	 */
	@Override
	public void set(Vbl val) {
		this.minMax.copy(((MinMaxVbl)val).minMax);
	}
	
	/**
	 * This method is called when reduction starts and all the threads have
	 * finished their work.
	 * 
	 * @param val Always an object of interface Vbl is passed
	 * 
	 * @return None
	 */
	@Override
	public void reduce(Vbl val) {
	}
	
	/**
	 * This is an inner class of MinMaxVbl which overrides just the reduce method
	 * and calls updateMin method for reduction. Minimum of two objects is chosen.
	 * @author Lokesh Agrawal
	 * Version: 28-SEP-2016
	 */
	public static class MinVbl extends MinMaxVbl{
		
		/**
		 * Constructor
		 * @param val  Object of MinMax
		 */
		public MinVbl(MinMax val) {
			super(val);
		}
		
		/**
		 * Overrided reduce method of parent class
		 * @param Vbl common vbl object is passed which needs to be typecasted
		 */
		public void reduce(Vbl vbl){
			this.minMax.updateMin(((MinMaxVbl)vbl).minMax);
		}
	}

	/**
	 * This is an inner class of MinMaxVbl which overrides just the reduce method
	 * and calls updateMax method for reduction. Maximum of two objects is chosen.
	 * @author Lokesh Agrawal
	 * Version: 28-SEP-2016
	 */
	public static class MaxVbl extends MinMaxVbl{
		
		/**
		 * Constructor
		 * @param val  Object of MinMax
		 */
		public MaxVbl(MinMax val) {
			super(val);
		}
		
		/**
		 * Overrided reduce method of parent class
		 * @param Vbl common vbl object is passed which needs to be typecasted
		 */
		public void reduce(Vbl vbl){
			this.minMax.updateMax(((MinMaxVbl)vbl).minMax);
		}
	}
}



