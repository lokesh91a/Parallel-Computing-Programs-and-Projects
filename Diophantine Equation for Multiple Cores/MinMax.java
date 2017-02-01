/**
 * Minmax is the class which implements interface cloneable.
 * It is the actual object on which reduction will be working at the end and this
 * class contains the logic for reduction.
 * 
 * author: Lokesh Agrawal id: la4401
 */
public class MinMax implements Cloneable {
	
	//Variables of type long for storing minimum solution
	private long x, y, z;

	/**
	 * Default constructor in which all variables are initialized
	 */
	public MinMax(long val) {
		//All min variables are initialized to Max value of long
		this.x = val;
		this.y = val;
		this.z = val;
	}

	/**
	 * Get method for variable x
	 * @return long x value of x
	 */
	public long getX() {
		return this.x;
	}


	/**
	 * Get method for variable y
	 * @return long y value of y
	 */
	public long getY() {
		return this.y;
	}

	/**
	 * Get method for variable z
	 * @return long z value of z
	 */
	public long getZ() {
		return this.z;
	}

	/**
	 * Set method for variable x
	 * @param long val x is set to val
	 */
	public void setX(long val) {
		this.x = val;
	}

	/**
	 * Set method for variable y
	 * @param long val y is set to val
	 */
	public void setY(long val) {
		this.y = val;
	}

	/**
	 * Set method for variable z
	 * @param long val z is set to val
	 */
	public void setZ(long val) {
		this.z = val;
	}

	/**
	 * This method is called when clone() method is called or when new thread
	 * local variable is created. This method is also used to copy values from
	 * one object of MinMax to a object
	 * @param MinMax val an object of MinMax class is passed from which all 
	 * 		  values are copies into respective variables of this class object
	 * @return object of this class is returned as this will call objects 
	 * 		   toString() method.
	 */
	public MinMax copy(MinMax val) {
		this.x = val.x;
		this.y = val.y;
		this.z = val.z;
		return this;
	}

	/**
	 * This method is called whenever a new thread local variable of this type
	 * is created which in turn calls the copy method
	 * @exception throws exception CloneNotSupportedException if cloning is not
	 * 			  handled properly
	 */
	public Object clone() {
		try {
			//A new variable is created calling copy method and its object is 
			//returned
			MinMax val = (MinMax) super.clone();
			val.copy(this);
			return val;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Cloning not handled properly", e);
		}
	}

	/**
	 * This method is called by the reduce method of MinVbl class.
	 * @param val Object of Minmax
	 */
	public void updateMin(MinMax val){
		//Actual logic for reduction
		if((val.getX()<this.getX()) || (val.getX()==this.getX() && 
				val.getY()<this.getY()) || (val.getX()==this.getX() && 
				val.getY()==this.getY()&&val.getZ()<this.getZ())){
			//If condition is statisfied then copy method of the same class is
			//called to copy the values
			this.copy(val);
		}
	}
	
	/**
	 * This method is called by the reduce method of MaxVbl class.
	 * @param val Object of Minmax
	 */
	public void updateMax(MinMax val){
		//Actual logic for reduction
		if((val.getX()>this.getX()) || (val.getX()==this.getX() && 
				val.getY()>this.getY()) || (val.getX()==this.getX() && 
				val.getY()==this.getY()&&val.getZ()>this.getZ())){
			//If condition is statisfied then copy method of the same class is
			//called to copy the values
			this.copy(val);
		}
	}
}