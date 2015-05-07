package de.uni_koblenz.schemex.schema;

import java.util.*;

import org.semanticweb.yars.nx.Literal;

import de.uni_koblenz.schemex.SchemEx;
import de.uni_koblenz.schemex.cache.Datasource;
import de.uni_koblenz.schemex.cache.Link;
import de.uni_koblenz.schemex.cache.LinkSet;
import de.uni_koblenz.schemex.util.Hash;
import de.uni_koblenz.schemex.util.Pair;

/**
 * Represents an equivalence class that's used to model the schema index.
 * 
 * @author Mathias
 * 
 */
public class EquivalenceClass {
	/**
	 * hash of the type cluster. generate new URI or blank node
	 */
	protected int type_cluster_hash;
	/**
	 * Set of links from an equivalence class to other type clusters.
	 */
	protected LinkSet links;
	/**
	 * Set of datasources that contain informations about instances that belong
	 * to this equivalence class
	 */
	protected Map<Datasource, Integer> datasources;

	// /**
	// * Number of data sources
	// */
	// protected int datasource_count = 0;

	/**
	 * instance count
	 */
	protected int instance_count = 1;

	/**
	 * number of flushed data sources
	 */
	protected int flushed_datasources = 0;

	/**
	 * Contains all objects contained in this EQC, which's predicate is of the
	 * type rdfs:label. This is used to provide a fast overview over the content
	 */
	protected Map<String, Map<String, Set<String>>> snippets;

	protected int numOfSnippets = 0;

	/**
	 * Creates a new equivalence class
	 * 
	 * @param _type_cluster_uri
	 *            The URI of the type cluster the equivalence class belongs to.
	 * @param _links
	 *            The set of links from an equivalence class to other type
	 *            clusters.
	 */
	public EquivalenceClass(int _type_cluster_hash, LinkSet _links) {
		type_cluster_hash = _type_cluster_hash;
		links = _links;
		datasources = new TreeMap<Datasource, Integer>();
		snippets = new HashMap<String, Map<String, Set<String>>>();
	}

	/**
	 * Adds a new link to an equivalence class
	 * 
	 * @param _link
	 *            Link-object (pair of property and object URI) to be added to
	 *            an equivalence class
	 * @return <code>true</code> if this set did not already contain the
	 *         specified element
	 */
	public boolean addLink(Link _link) {
		return links.add(_link);
	}

	/**
	 * Adds a new datasource to the map of datasource-instance pairs
	 * 
	 * @param _datasource
	 *            URI of the datasource
	 * 
	 */
	public void addDatasource(Datasource _datasource) {
		// if (datasources.add(_datasource)) {
		// datasource_count++;
		// return true;
		// } else {
		// return false;
		// }
		if (datasources.containsKey(_datasource)) {
			datasources.put(_datasource, datasources.get(_datasource) + 1);

		} else {
			datasources.put(_datasource, 1);

		}
	}

	/**
	 * Adds a new datasource-collection to the map of datasource-instance pairs
	 * 
	 * @param _datasource
	 *            set of datasource URIs
	 */
	public void addDatasources(Collection<Datasource> _datasources) {
		// int count_before = datasources.size();
		// boolean result;
		// result = datasources.addAll(_datasources);
		// datasource_count += _datasources.size() - count_before;
		// return result;
		for (Datasource ds : _datasources) {
			addDatasource(ds);
		}
	}

	/**
	 * Flushs all datasources, but keeps the datasource count
	 * 
	 */
	public void flushDatasources() {
		flushed_datasources += datasources.size();
		datasources.clear();
	}

	/**
	 * Return an individual equivalence URI based on the type-cluster hash and
	 * the outgoing links
	 * 
	 * @return equivalence class URI
	 */
	public String getURI() {
		return SchemaConstants.EQC_URI_PREFIX
				+ Hash.md5(Integer.toString(getLinks().hashCode()
						- type_cluster_hash));
	}

	public String getPostfix() {

		return Hash.md5(Integer.toString(getLinks().hashCode()
				- type_cluster_hash));

	}

	/**
	 * Increments the instance count by 1
	 * 
	 * @return instance count
	 */
	public int incInstanceCount() {
		return instance_count++;
	}

	/**
	 * Returns the instance count for this equivalence class
	 * 
	 * @return instance count
	 */
	public int getInstanceCount() {
		return instance_count;
	}

	/**
	 * Returns the datasource count for this equivalence class
	 * 
	 * @return instance count
	 */
	public int getDatasourceCount() {
		// return datasource_count;
		return datasources.size() + flushed_datasources;
	}

	/**
	 * Returns the number of links for this equivalence class
	 * 
	 * @return instance count
	 */
	public int getLinkCount() {
		return links.size();
	}

	/**
	 * compares two equivalence classes by their linksets
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		try {
			EquivalenceClass eq = (EquivalenceClass) obj;
			return eq.links.equals(this.links);
		} catch (Exception e) {
			System.out.println("Object is no equivalence class: "
					+ e.getMessage());
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getLinks().hashCode();
	}

	/*
	 * getters and setters
	 */

	public int getTypeClusterHash() {
		return type_cluster_hash;
	}

	public void setTypeClusterHash(int _type_cluster_hash) {
		this.type_cluster_hash = _type_cluster_hash;
	}

	public LinkSet getLinks() {
		return links;
	}

	public void setLinks(LinkSet links) {
		this.links = links;
	}

	public Map<Datasource, Integer> getDatasources() {
		return datasources;
	}

	/**
	 * Provides a Set-view of the EQC's associated datasources
	 * 
	 * @return Set containing this EQC's datasources
	 */
	public Set<Datasource> getDatasourcesAsSet() {
		return datasources.keySet();
	}

	public void setDatasources(Map<Datasource, Integer> datasources) {
		this.datasources = datasources;
	}

	public void addSnippet(String source, String instanceUri, String snippet) {

		if (numOfSnippets >= SchemaConstants.MAX_SNIPPETS)
			return;
		if (snippets.containsKey(source)) {
			// Instances in this datasource
			Map<String, Set<String>> p = snippets.get(source);
			Set<String> s;
			if (p.containsKey(instanceUri)) {
				s = p.get(instanceUri);
				if (s == null) {
					s = new TreeSet<String>();
				}

			} else {
				s = new TreeSet<String>();
				p.put(instanceUri, s);
			}
			s.add(SchemEx.normalizeLiteral(snippet));
			numOfSnippets++;

		} else {

			Map<String, Set<String>> p = new HashMap<String, Set<String>>();
			snippets.put(source, p);
			Set<String> s = new TreeSet<String>();
			p.put(instanceUri, s);
			s.add(SchemEx.normalizeLiteral(snippet));
			numOfSnippets++;
		}
	}

	public void addAllSnippets(String source, String instanceUri,
			Set<String> snippets) {

		if (source == null || instanceUri == null || snippets == null)
			return;
		for (String sn : snippets) {
			addSnippet(source, instanceUri, sn);
		}
		// if (this.snippets.containsKey(source)) {
		// if (snippets != null) {
		// for (String sn : snippets) {
		//
		// Map<String, Set<String>> p = this.snippets.get(source);
		// if (p.get(instanceUri))
		// if (this.snippets.get(source).size() >= SchemaConstants.MAX_SNIPPETS)
		// break;
		// {
		//
		// this.snippets.get(source).add(
		// SchemEx.normalizeLiteral(sn));
		// }
		// }
		// }
		// } else {
		// Set<String> set = new TreeSet<String>();
		//
		// if (snippets != null) {
		// for (String sn : snippets) {
		// if (set.size() >= SchemaConstants.MAX_SNIPPETS)
		// break;
		// {
		// set.add(SchemEx.normalizeLiteral(sn));
		// }
		// }
		// }
		// this.snippets.put(source, set);
		// }

	}

	public Map<String, Map<String, Set<String>>> getSnippets() {
		return snippets;
	}

}
