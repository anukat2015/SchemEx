package de.uni_koblenz.schemex.cache;

import java.util.*;

import org.semanticweb.yars.nx.Node;

import de.uni_koblenz.schemex.schema.SchemaConstants;
import de.uni_koblenz.schemex.util.Hash;

/**
 * RDF instance including all RDF types, outgoing links (properties + objects)
 * and datasources containing triples about this instance.
 * 
 * @author Mathias
 * 
 */
public class InstanceFull extends InstanceSimple {
	/**
	 * set of links to other instances (properties + objects)
	 */
	protected LinkSet links;

	/**
	 * provides provenance for snippets
	 */
	protected Map<Datasource, Set<String>> snippets;

	/**
	 * Creates a new RDF instance with types, links and datasources
	 * 
	 * @param _instance_uri
	 *            subject URI of the instance
	 */
	public InstanceFull(String _instance_uri) {
		//
		super(_instance_uri);
		links = new LinkSet();
		snippets = new TreeMap<Datasource, Set<String>>();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Adds a new link-object (pair of property and object) to the instance
	 * 
	 * @param _property_uri
	 *            property-URI
	 * @param _object_uri
	 *            object-URI
	 * @return <code>true</code> if this set did not already contain the
	 *         specified element
	 */
	public boolean addLink(Node _property, Node _object) {
		return links.add(new Link(_property, _object));
	}

	/**
	 * Adds a new datasource URI to the RDF instance. This may be an RDF
	 * instance URI, an RDF file URI or a SPARQL-endpoint / DESCRIBE-Query
	 * 
	 * @param _datasource_uri
	 *            URI of the datasource
	 * @return true if this set did not already contain the specified element
	 */
	public boolean addDatasource(Datasource _datasource) {
		if (snippets.containsKey(_datasource))
			return false;
		else {
			snippets.put(_datasource, null);
			return true;

		}
	}

	/**
	 * Creates a MD5 hash value based on the footprint / signature of the
	 * outgoing links from an instance to other objects.
	 * 
	 * @return MD5 hash value
	 */
	protected String getEqClassHash() {
		return Hash.md5(Integer.toString(links.hashCode()));
	}

	/**
	 * Creates an RDF URI for an equivalence class based on a URI prefix and a
	 * MD5 hash value.
	 * 
	 * @return generated URI for a specific equivalence class
	 */
	public String getEqClassURI() {
		return SchemaConstants.EQC_URI_PREFIX + getEqClassHash();
	}

	/**
	 * Return an hash value based on the outgoing links
	 * 
	 * @return hash value
	 */
	public int getLinksHash() {
		return links.hashCode();
	}

	/*
	 * getters
	 */
	public Set<Link> getLinks() {
		return links;
	}

	public Set<Datasource> getDatasources() {
		return snippets.keySet();
	}

	public void addSnippet(Datasource source, String snippet) {
		if (snippets.containsKey(source)) {
			Set<String> set = snippets.get(source);
			if(set==null) {
				set = new TreeSet<String>();
				snippets.put(source, set);
			}
			set.add(snippet);
		} else {
			Set<String> set = new TreeSet<String>();
			set.add(snippet);
			snippets.put(source, set);
		}
	}

	public Map<Datasource, Set<String>> getSnippets() {
		return snippets;
	}

}
