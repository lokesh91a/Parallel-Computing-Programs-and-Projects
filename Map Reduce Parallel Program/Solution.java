import java.io.IOException;
import edu.rit.io.InStream;
import edu.rit.io.OutStream;
import edu.rit.pj2.Tuple;
import edu.rit.pj2.Vbl;

/**
 * Class Solution provides two long integer 
 * and these are shared by multiple threads.
 * 
 * @author Lokesh Agrawal
 * @Version 25-NOV-2016
 *
 */
public class Solution extends Tuple implements Vbl {
	/**
	 * The shared long integer items.
	 */
	public long index, score;

	// Exported constructors.

	/**
	 * Construct a new shared long integer variable.
	 * 
	 * @param index index value
	 * 		  score score value
	 */
	public Solution(long index, long score) {
		this.index = index;
		this.score = score;
	}

	/**
	 * Default Constructor
	 */
	public Solution() {
		this.index = 0;
		this.score = 0;
	}

	/**
	 * Returns the long integer value of index
	 * @return Long integer value.
	 */
	public long indexValue() {
		return this.index;
	}

	/**
	 * Returns the long integer value of score
	 * @return Long integer value.
	 */
	public long scoreValue() {
		return this.score;
	}

	/**
	 * Set the fields in this shared variable to the respective fields in
	 * the given shared variable. This variable must be set to a deep copy 
	 * of the given variable.
	 *
	 * @param vbl   Shared variable.
	 */
	public void set(Vbl vbl) {
		this.index = ((Solution) vbl).indexValue();
		this.score = ((Solution) vbl).scoreValue();
	}

	/**
	 * Reduction is performed by this reduce method. Reduce the given shared 
	 * variable into this shared variable.
	 *
	 * @param vbl    Shared variable.
	 */
	public void reduce(Vbl vbl) {
		long inIndex = ((Solution) vbl).indexValue();
		long inScore = ((Solution) vbl).scoreValue();
		if((this.index>inIndex && this.score==inScore)
				|| (inScore>this.score))
		this.set(vbl);	
	}

	/**
	 * Returns a string version of this shared variable which specifies 2 
	 * fields of this shared variable.
	 *
	 * @return String version.
	 */
	public String toString() {
		return "Index: "+this.index+" Score: "
	+this.score;
	}

	/**
	 * Write this object's fields to the given out stream.
	 *
	 * @param out	Out stream.
	 *
	 * @exception IOException	Thrown if an I/O error occurred.
	 */
	public void writeOut(OutStream out) throws IOException {
		out.writeLong(this.index);
		out.writeLong(this.score);
	}

	/**
	 * Read this object's fields from the given in stream.
	 *
	 * @param in	In stream.
	 *
	 * @exception IOException	Thrown if an I/O error occurred.
	 */
	public void readIn(InStream in) throws IOException {
		this.index = in.readLong();
		this.score = in.readLong();
	}
}