import java.io.IOException;
import edu.rit.io.InStream;
import edu.rit.io.OutStream;
import edu.rit.pj2.Tuple;
import edu.rit.pj2.Vbl;

/**
 * Class Solution provides two long integer which supports only addition
 * operation while reduction and these are shared by multiple threads.
 * 
 * @author Lokesh Agrawal
 * @Version 12-OCT-2016
 *
 */
public class Solution extends Tuple implements Vbl {
	/**
	 * The shared long integer items.
	 */
	public long triangle, triplet;

	// Exported constructors.

	/**
	 * Construct a new shared long integer variable. The item's initial value is
	 * 0L.
	 */
	public Solution() {
		this.triangle = 0L;
		this.triplet = 0L;
	}

	/**
	 * Construct a new shared long integer variable with the given initial
	 * value.
	 *
	 * @param value		Initial value.
	 */
	public Solution(long value) {
		this.triangle = value;
		this.triplet = value;
	}

	/**
	 * Returns the long integer value of item triangle(Number of triangles).
	 * @return Long integer value.
	 */
	public long triangleValue() {
		return this.triangle;
	}

	/**
	 * Returns the long integer value of item triplets(Number of triplets).
	 * @return Long integer value.
	 */
	public long tripletValue() {
		return this.triplet;
	}

	/**
	 * Set the fields in this shared variable to the respective fields in
	 * the given shared variable. This variable must be set to a deep copy 
	 * of the given variable.
	 *
	 * @param vbl   Shared variable.
	 */
	public void set(Vbl vbl) {
		this.triangle = ((Solution) vbl).triangleValue();
		this.triplet = ((Solution) vbl).tripletValue();
	}

	/**
	 * Reduction is performed by this reduce method. Reduce the given shared 
	 * variable into this shared variable. The two fields in the shared variables
	 * are added respectively with each other and the result is stored in this 
	 * shared variable.
	 *
	 * @param vbl    Shared variable.
	 *
	 * @exception ClassCastException
	 *                (unchecked exception) Thrown if the class of <TT>vbl</TT>
	 *                is not compatible with the class of this shared variable.
	 */
	public void reduce(Vbl vbl) {
		this.triangle += ((Solution) vbl).triangleValue();
		this.triplet += ((Solution) vbl).tripletValue();
	}

	/**
	 * Returns a string version of this shared variable which specifies 2 
	 * fields of this shared variable.
	 *
	 * @return String version.
	 */
	public String toString() {
		return "Number of Triangles: "+this.triangle+" Number of Triplets: "
	+this.triplet;
	}

	/**
	 * Write this object's fields to the given out stream.
	 *
	 * @param out	Out stream.
	 *
	 * @exception IOException	Thrown if an I/O error occurred.
	 */
	public void writeOut(OutStream out) throws IOException {
		out.writeLong(this.triangle);
		out.writeLong(this.triplet);
	}

	/**
	 * Read this object's fields from the given in stream.
	 *
	 * @param in	In stream.
	 *
	 * @exception IOException	Thrown if an I/O error occurred.
	 */
	public void readIn(InStream in) throws IOException {
		this.triangle = in.readLong();
		this.triplet = in.readLong();
	}
}