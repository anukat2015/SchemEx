package de.uni_koblenz.schemex;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.LogManager;
import org.semanticweb.yars.nx.BNode;
import org.semanticweb.yars.nx.BooleanLiteral;
import org.semanticweb.yars.nx.DateTimeLiteral;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.NumericLiteral;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.Triple;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.parser.ParseException;

import com.sun.org.apache.xml.internal.utils.NSInfo;

import de.uni_koblenz.schemex.cache.*;
import de.uni_koblenz.schemex.schema.*;
import de.uni_koblenz.schemex.util.Hash;
import de.uni_koblenz.schemex.util.Input;
import de.uni_koblenz.schemex.util.MemUsage;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

/**
 * Class for extracting a schema from NQuads
 * 
 * @author Mathias
 * 
 */
public class SchemEx implements SchemExInterface {

	/**
	 * logger
	 */
	private static Logger logger = LogManager.getRootLogger();

	/**
	 * Schema
	 */
	protected Schema schema;
	/**
	 * Cache
	 */
	protected InstanceCache cache;
	/**
	 * Backward-Cache
	 */
	protected InstanceCache backward_cache;

	/**
	 * Writer Factory
	 */
	protected SchemaWriterFactory writerFac;
	/**
	 * NQuad parser
	 */
	protected NxParser nxp;

	/**
	 * extract RDFS triples
	 */
	protected boolean extract_rdfs = false;

	/**
	 * Flag whether to ignore all rdf:type information. Literally this is
	 * currently implemented by skipping all Triples with an rdf:type
	 * information when reading from the stream. This approach is not optimal,
	 * because if these are the only triples about a resources, this resource
	 * will not be counted.
	 * 
	 */
	protected boolean ignore_types = false;

	/**
	 * The strategy to be used to determine, which equivalence classes should be
	 * flushed
	 */
	protected FlushStrategy fs;

	public boolean isIgnore_types() {
		return ignore_types;
	}

	@Override
	public void setIgnore_types(boolean ignore_types) {
		this.ignore_types = ignore_types;
	}

	long triple_count = 0;
	long start_time = 0;
	long end_time = 0;

	/**
	 * Creates a Schema Extractor with a specified InputStream
	 * 
	 * @param fac
	 *            The {@link SchemaWriterFactory} to be used for writing
	 * @throws ParseException
	 * @throws IOException
	 */
	public SchemEx(SchemaWriterFactory fac, FlushStrategy fs)
			throws ParseException, IOException {
		this(CacheConstants.STD_CACHESIZE, CacheConstants.STD_CACHESIZE, fac,
				fs);
	}

	/**
	 * Creates a schema extractor with a given cachesize and the Input
	 * 
	 * @param _cachesize
	 *            size of the instance cache
	 * @param _bw_cachesize
	 *            size of the backward instance cache
	 * @param fac
	 *            The {@link SchemaWriterFactory} to be used for writing
	 * @throws ParseException
	 * @throws IOException
	 */
	public SchemEx(int _cachesize, int _bw_cachsize, SchemaWriterFactory fac,
			FlushStrategy fs) throws ParseException, IOException {
		cache = new FifoCache(_cachesize);
		backward_cache = new FifoCache(_bw_cachsize);
		writerFac = fac;
		schema = new Schema(fac);
		this.fs = fs;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.schemex.SchemExInterface#startProcess()
	 */
	@Override
	public void startProcess() {
		logger.info("Schema extraction process started.");
		// runtime measurement, set start time
		start_time = System.currentTimeMillis();

	}

	/**
	 * This method performs some post-processing actions like runtime
	 * calculation, entity counts, etc.
	 * 
	 * @throws Exception
	 */
	public void stopProcess() {
		logger.info("Start post-processing.");
		// process the instances that remained in the cache
		flushCacheToSchema();

		printStats();
	}

	@Override
	public void processStream(Input _input) throws Exception {
		InputStream _in = _input.getInputStream();

		nxp = new NxParser(_in);
		// read NQuads
		while (nxp.hasNext()) {
			processNQuad(nxp.next());
		}
		logger.info("No more nquads.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.schemex.SchemExInterface#processNQuad(org.semanticweb.
	 * yars.nx.Node[])
	 */
	@Override
	public void processNQuad(Node[] _ns) throws Exception {
		// check, if _ns is a NQuad
		if (triple_count == 0 && _ns.length < 4) {
			logger.warn("Input is no NQuad. Using subject as context value.");
		}

		if (_ns.length < 3) {
			logger.warn("Error in Input. Skipping this line");
			return;
		}
		// inc triple count
		triple_count++;

		// map the NQuad on 4 attributes (to avoid mistakes with array index)
		Node subject = _ns[0];
		Node property = _ns[1];
		Node object = _ns[2];
		Node context;
		if (_ns.length < 4) {
			context = _ns[0];
		} else {
			context = _ns[3];
		}

		// de-anonymizing blank nodes (adding blank node id with
		// context-hash-suffix)
		if (subject instanceof BNode) {
			String subString = subject.toString();
			if (subString.lastIndexOf('#') == subString.length() - 1
					|| subString.lastIndexOf('/') == subString.length() - 1)
				subject = new Resource(context.toString() + subject);

			else
				subject = new Resource(context.toString() + "#" + subject);
		}
		if (object instanceof BNode) {
			object = new Resource(context.toString() + "#" + object);
			String obString = object.toString();
			if (obString.lastIndexOf('#') == obString.length() - 1
					|| obString.lastIndexOf('/') == obString.length() - 1)
				object = new Resource(context.toString() + object);

			else
				object = new Resource(context.toString() + "#" + object);
		}
		// If object is a literal, normalize it's content
		if (object instanceof Literal) {
			object = new Literal((SchemEx.normalizeLiteral(((Literal) object)
					.toString())));
		}

		InstanceFull instance = null;
		boolean in_cache = false;

		// check, if the subject is already in the cache, otherwise create a new
		// instance
		if (cache.containsInstance(subject.toString())) {
			// read instance from cache
			instance = (InstanceFull) cache.getInstance(subject.toString());
			in_cache = true;
		} else {
			// create new instance
			instance = new InstanceFull(subject.toString());
		}

		// property is rdf:type
		if (property.toString().equals(SchemaConstants.RDF_TYPE)) {
			if (!this.ignore_types) {
				instance.addType(object.toString());
			}
		}

		// property is an RDFS property (subclassof, subpropertyof, etc.)
		else {
			if (SchemaConstants.RDFS_PROPERTIES.contains(property.toString())) {
				if (extract_rdfs) {
					schema.addRDFSTriple(new Triple(subject, property, object));
				}
			} else {
				// otherwise
				instance.addLink(property, object);
			}

		}
		if (property.toString().equals(SchemaConstants.RDFS_SUBCLASSOF)) {
			schema.addSubclass(subject.toString(), object.toString());
		}

		Datasource ds = new Datasource(context.toString());
		// property is rdfs:label
		if (SchemaConstants.SNIPPET_PROPERTIES.contains(property.toString())) {
			if (object instanceof Literal) {
				Literal l = (Literal) object;
				instance.addSnippet(ds, l.getData());

			}

		}
		// add the datasource to the instance
		instance.addDatasource(ds);
		// add the instance to the cache
		if (!in_cache) {
			if (!cache.add(instance)) {
				/*
				 * removing the first instance in the queue, derive the schema
				 * concepts and add them to the schema
				 */
				// remove the first instance
				InstanceFull removed_instance = (InstanceFull) cache.remove();

				// add the removed instance to the backward cache
				InstanceTypeHash backward_instance = new InstanceTypeHash(
						removed_instance);
				if (!backward_cache.add(backward_instance)) {
					backward_cache.remove();
					backward_cache.add(backward_instance);
				}

				// derive the schema concepts
				deriveConceptsFromInstance(removed_instance);

				// add the new instance
				cache.add(instance);
			}
		}

		/*
		 * Console output for visual process control
		 */

		if (triple_count % 10000 == 0) {
			System.out.print(".");
		}

		if (triple_count % 100000 == 0) {
			double triples_per_second = triple_count
					/ ((System.currentTimeMillis() - start_time) / 1000);
			System.out.println(" " + triple_count + " " + MemUsage.getUsed()
					+ "MB " + triples_per_second);
		}

	}

	/**
	 * This method derives the schema concepts from instances that are removed
	 * from the instance cache.
	 * 
	 * @param _instance
	 *            an instance removed from the instance cache
	 */
	protected void deriveConceptsFromInstance(InstanceFull _instance) {
		// debug
		logger.debug("processing instance: " + _instance.getInstanceURI()
				+ " #types: " + _instance.getTypes().size() + " tc-uri: "
				+ _instance.getTypeClusterURI() + " #links: "
				+ _instance.getLinks().size() + " #ds: "
				+ _instance.getDatasources().size());

		// type cluster URI, set on the generated URI
		int tc_hash = _instance.getTypesHash();
		// type cluster object
		TypeCluster tc;
		// equivalence class object
		EquivalenceClass eq;
		// set of schema-links
		LinkSet links;

		// set of snippets
		// checks, if the schema contains the type cluster
		if (schema.containsTypeCluster(tc_hash)) {
			// return the type cluster, if found
			tc = schema.getTypeCluster(tc_hash);
			logger.debug("type cluster found in schema; #eq-classes: "
					+ tc.getEqClassCount());
			// increment the instance count for that type cluster
			tc.incInstanceCount();
		} else {
			// create new type cluster and add it to the schema
			tc = new TypeCluster(_instance.getTypes());
			schema.addTypeCluster(tc);
			logger.debug("new type cluster, hash: " + tc_hash);
		}

		// create new link set for the equivalence class
		links = new LinkSet();
		// does the instance have outgoing links?
		if (_instance.getLinks().size() > 0) {
			// try to resolve the objects of each link to their type clusters
			for (Link l : _instance.getLinks()) {
				// check, whether object is literal
				if (l.getObject() instanceof Literal) {
					Node literal_type = determineLiteral((Literal) l
							.getObject());
					links.add(new Link(l.getProperty(), literal_type));
				} else {
					// check, whether the instance is contained by the cache
					if (cache.containsInstance(l.getObject().toString())) {
						// creates a new link
						Instance instance = cache.getInstance(l.getObject()
								.toString());

						links.add(new Link(l.getProperty(), new Resource(
								instance.getTypesPostfix())));
					} else {
						// check, whether the instance has already been moved
						// from the main cache to the backwards cache
						if (backward_cache.containsInstance(l.getObject()
								.toString())) {
							Instance i = backward_cache.getInstance(
									l.getObject().toString());
							// creates a new link
							links.add(new Link(l.getProperty(), new Resource(
									i
											.getTypesPostfix())));
						} else {
							// set object to "not resolved"
							links.add(new Link(l.getProperty(), new Resource(
									"notresolved")));
						}

					}
				}

			}
		}

		/*
		 * create equivalence class for this type cluster and with set of
		 * outgoing links to other type clusters
		 */
		// generate eq-class URI via MD5-hash of the linkset and the type
		// cluster
		int eq_hash = links.hashCode();
		// check, whether equivalence class already exists
		if (tc.containsEqClass(eq_hash)) {
			// get EQ class
			eq = tc.getEqClass(eq_hash);
			logger.debug("eq class found: " + eq_hash);
			// inc instance count for this EQC
			eq.incInstanceCount();
		} else {
			// create new equivalence class
			eq = new EquivalenceClass(tc_hash, links);
			// add the equivalence class to the type cluster
			tc.addEqClass(eq);
			logger.debug("eq class created: " + eq_hash);
		}
		// add the datasources to the eq class
		eq.addDatasources(_instance.getDatasources());
		// add snippets to the EQC, if any where found
		for (Map.Entry<Datasource, Set<String>> entry : _instance.getSnippets()
				.entrySet()) {
			if (entry.getValue() == null || entry.getValue().size() > 0)
				eq.addAllSnippets(entry.getKey().getURI(),
						_instance.getInstanceURI(), entry.getValue());
		}

		// set _instance to null --> garbage collection
		_instance = null;
		logger.debug("############ instance processed.");
	}

	/**
	 * Flush the cache after processing all NQuads and derive the schema
	 * concepts
	 * 
	 * @throws Exception
	 */
	protected void flushCacheToSchema() {
		// remove all instances until the cache is empty
		while (!cache.isEmpty()) {
			// derive schema concepts
			try {
				InstanceFull removed_instance = (InstanceFull) cache.remove();

				// add the removed instance to the backward cache
				InstanceTypeHash backward_instance = new InstanceTypeHash(
						removed_instance);
				if (!backward_cache.add(backward_instance)) {
					backward_cache.remove();
					backward_cache.add(backward_instance);
				}
				deriveConceptsFromInstance(removed_instance);
			} catch (Exception e) {
				logger.fatal("No full instance object.");
			}

		}
	}

	/**
	 * Determines the literal type of the given node
	 * 
	 * @param _node
	 *            RDF node
	 * @return literal type
	 */
	protected Node determineLiteral(Node _node) {
		if (_node instanceof BooleanLiteral) {
			return BooleanLiteral.BOOLEAN;
		} else if (_node instanceof NumericLiteral) {
			return ((NumericLiteral) _node).getDatatype();
		} else if (_node instanceof DateTimeLiteral) {
			return DateTimeLiteral.DATETIME;
		} else if (_node instanceof Literal) {
			return Literal.STRING;
		}
		return _node;
	}

	@Override
	public void setRDFSExtraction() {
		extract_rdfs = true;
	}

	/**
	 * prints statistics via logger info function
	 */
	@Override
	public void printStats() {
		end_time = System.currentTimeMillis();
		logger.info("runtime: " + ((end_time - start_time) / 1000) + "sec");
		logger.info("#instances: " + schema.getInstanceCount());
		logger.info("#data sources: " + schema.getDatasourceCount());
		logger.info("#type cluster: " + schema.getTypeClusterCount());
		logger.info("#eq classes: " + schema.getEqClassCount());
		logger.info("#rdfs triples: " + schema.getRDFSTripleCount());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.schemex.SchemExInterface#flushDatasourcesToFile(java.lang
	 * .String)
	 */
	@Override
	public void flushDatasourcesToFile(String _name) {
		schema.flushDatasources(_name + "/datasources.nt");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.schemex.SchemExInterface#flushSchemaToFile(java.lang.String
	 * , int)
	 */
	@Override
	public void flushSchemaToFile(String _name) {
		schema.flushSchema(_name + "/schema.nt", fs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.schemex.SchemExInterface#writeSchemaToFile(java.lang.String
	 * )
	 */
	@Override
	public void writeSchemaToFile(String _name) {
		schema.writeSchema(_name + "/schema.nt");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.schemex.SchemExInterface#writeAdditionalFiles(java.lang
	 * .String)
	 */
	@Override
	public void writeAdditionalFiles(String _name) {
		schema.writeTypeClusterCSV(_name + "/tc.csv");
		schema.writeTypeClusterURIs(_name + "/tc_uris.txt");
		schema.writeEquivalenceClassURIs(_name + "/eqc_uris.txt");
		schema.writeInstancesPerEQCDatasources(_name + "/instance_EQC-Ds.txt");
		schema.writeStatistics(_name);
	}

	public static String normalizeLiteral(String literal) {
		String sn = literal.replace("\\r", " ");
		sn = sn.replace("\\n", " ");

		// Try to remove unnecessary backspaces
		String sTemp = Literal.unescape(sn);
		// In case, there was some error, use previous result to escape
		if (sTemp == null)
			sn = Literal.escapeForNx(sn);
		else
			sn = Literal.escapeForNx(sTemp);

		// Remove whitespaces
		sn = sn.replaceAll("\\s+", " ");

		return sn;

	}
}
