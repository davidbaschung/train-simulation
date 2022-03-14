package railway;

/**
 * Abstract element of the railway. provides the core id-system.
 */
public abstract class RailwayElement {

	/**
	 * the string id of this RailwayElement
	 */
	String id;

	/**
	 * @return the id of this RailwayElement
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return a string identifying the railway element containing its class and id
	 * like  'MyClass[myid]' .
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + this.getId() + "]";
	}
}
