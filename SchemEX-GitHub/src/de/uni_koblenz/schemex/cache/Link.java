package de.uni_koblenz.schemex.cache;

import java.util.Comparator;

import org.semanticweb.yars.nx.Node;

/**
 * Class to represent links between RDF instances via pairs of properties and
 * objects
 * 
 * @author Mathias
 * 
 */
public class Link implements Comparable<Link> {
	// property
	Node property;

	// object
	Node object;

	/**
	 * Creates a new link (pair / tupel of property and object)
	 * 
	 * @param _property
	 *            property node
	 * @param _object
	 *            object node
	**/
	public Link(Node _property, Node _object) {
		super();
		property = _property;
		object = _object;
	}
	
	/**
	 * Returns a string with the concatenated attributes
	 * 
	 * @return concatenated property and object URIs
	 */
	protected String getConcatenatedString() {
		return getProperty().toString() + getObject().toString();
	}
	
	@Override
	public String toString() {
		return getConcatenatedString();
	}
	
	/**
	 * Creates a hash-value for this Link
	 * 
	 * @return hash value
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getConcatenatedString().hashCode();
	}

	@Override
	public int compareTo(Link o) {
		return this.getConcatenatedString().compareTo(o.getConcatenatedString());
	}

	public class LinkComparator implements Comparator<Link> {
		@Override
		public int compare(Link o1, Link o2) {
			
			return o1.compareTo(o2);
		}

	}
	
	/*
	 * getters and setters
	 */
	
	public Node getProperty() {
		return property;
	}

	public Node getObject() {
		return object;
	}

	public void setProperty(Node property) {
		this.property = property;
	}

	public void setObject(Node object) {
		this.object = object;
	}

}
