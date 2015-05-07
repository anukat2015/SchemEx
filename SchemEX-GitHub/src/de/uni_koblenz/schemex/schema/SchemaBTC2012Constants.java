package de.uni_koblenz.schemex.schema;

import java.util.HashSet;
import java.util.Set;

public class SchemaBTC2012Constants {

	// RDF(S) vocabulary
	public final static String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public final static String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
	public final static String RDFS_LITERAL = "http://www.w3.org/2000/01/rdf-schema#Literal";
	public final static String RDFS_SUBCLASSOF = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	public final static String RDFS_SUBPROPERTYOF = "http://www.w3.org/2000/01/rdf-schema#subPropertyOf";
	public final static String RDFS_DOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
	public final static String RDFS_RANGE = "http://www.w3.org/2000/01/rdf-schema#range";

	// void
	public final static String VOID_PREFIX = "http://rdfs.org/ns/void#";
	public static final Set<String> RDFS_PROPERTIES;
	static {
		RDFS_PROPERTIES = new HashSet<String>();
		RDFS_PROPERTIES.add(RDFS_SUBCLASSOF);
		RDFS_PROPERTIES.add(RDFS_SUBPROPERTYOF);
		RDFS_PROPERTIES.add(RDFS_DOMAIN);
		RDFS_PROPERTIES.add(RDFS_RANGE);
	}

	public static final Set<String> SNIPPET_PROPERTIES;
	static {
		SNIPPET_PROPERTIES = new HashSet<String>();
		SNIPPET_PROPERTIES.add(RDFS_LABEL);
		SNIPPET_PROPERTIES.add("http://www.w3.org/2004/02/skos/core#altLabel");
		SNIPPET_PROPERTIES.add("http://www.w3.org/2004/02/skos/core#prefLabel");
		SNIPPET_PROPERTIES.add("http://www.w3.org/TR/rdf-schema/#ch_comment");
		SNIPPET_PROPERTIES.add("http://purl.org/dc/terms/title");
		SNIPPET_PROPERTIES
				.add("http://dublincore.org/2010/10/11/dcterms.rdf#title");
	}

	/** RDF Schema */

	public final static String SCHEMEX = "http://schemex.west.uni-koblenz.de/";

	public final static String TRIPLE_PER_EQC_PER_DS_PROPERTY = SCHEMEX
			+ "entityCount";
	public final static String INSTANCE_COUNT_PREDICATE_URI = TRIPLE_PER_EQC_PER_DS_PROPERTY;

	public final static String EQC_TO_DS_LINK_PREDICATE = SCHEMEX
			+ "hasDataset";

	public final static String TC_TO_CLASS_PREDICATE_URI = SCHEMEX + "hasClass";

	public final static String TC_TYPE = SCHEMEX + "TypeCluster";
	public final static String EQC_TYPE = SCHEMEX + "EquivalenceClass";

	public final static String TC_TO_EQC_PREDICATE_URI = SCHEMEX + "hasSubset";

	public final static String EQC_TO_DS_LINK_TYPE = "http://rdfs.org/ns/void#Dataset";
	public final static String SNIPPET_PROPERTY = SCHEMEX + "exampleResource";

	public final static String EQC_TO_TC_LINK_TYPE = SCHEMEX
			+ "EquivalenceClassToTypeClusterLinkage";

	public final static String OBJECTS_TARGET_PREDICATE_URI = SCHEMEX
			+ "objectsTarget";
	public final static String SUBJECTS_TARGET_PREDICATE_URI = SCHEMEX
			+ "subjectsTarget";
	public final static String PROPERTY_PREDICATE_URI = SCHEMEX + "property";

	public final static String EQC_TO_DS_LINK_TO_DS_PREDICATE = VOID_PREFIX
			+ "uriLookupEndpoint";

	/** Instanzen **/

	public final static String EQC_TO_TC_LINK_URI_PREFIX = SCHEMEX + "ls";

	public final static String EQC_TO_DS_LINK_URI_PREFIX = SCHEMEX + "ls";
	public final static String LINKSET_URI_PREFIX = SCHEMEX + "ls";
	public final static String EQC_URI_PREFIX = SCHEMEX + "eq";
	public final static String TC_URI_PREFIX = SCHEMEX + "tc";

	public final static String TC_URI_NOT_RESOLVED = TC_URI_PREFIX
			+ "notresolved";
	public final static String TC_URI_NO_TYPES = TC_URI_PREFIX + "notypes";

	public final static String BLANK_NODES_URI = SCHEMEX + "bn";
}
