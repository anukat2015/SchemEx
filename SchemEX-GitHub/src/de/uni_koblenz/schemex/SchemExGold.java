package de.uni_koblenz.schemex;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.*;
import org.semanticweb.yars.nx.BNode;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.Triple;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.parser.ParseException;

import de.uni_koblenz.schemex.cache.*;
import de.uni_koblenz.schemex.schema.*;
import de.uni_koblenz.schemex.util.Input;
import de.uni_koblenz.schemex.util.MemUsage;
import de.uni_koblenz.schemex.util.strategies.FlushStrategy;

/**
 * Class for extracting a schema from NQuads
 * 
 * @author Mathias
 * 
 */
public class SchemExGold extends SchemEx {

	/**
	 * logger
	 */
	private static Logger logger = LogManager.getRootLogger();

	/**
	 * input file name
	 */
	protected String filename;

	/**
	 * Creates a Schema Extractor with a specified InputStream
	 * 
	 * @param _tc_cachesize
	 *            type-cluster cache (first run)
	 * @param _cachesize
	 *            instance cache (second run)
	 * @throws ParseException
	 * @throws IOException
	 */
	public SchemExGold(int _cachesize, int _tc_cachesize,
			SchemaWriterFactory fac, FlushStrategy fs) throws ParseException,
			IOException {
		super(_cachesize, _tc_cachesize, fac, fs);
	}

	@Override
	public void processStream(Input _input) throws Exception {

		InputStream _in = _input.getInputStream();
		if (_in == null)
			throw new IOException("Stream could not be created");
		nxp = new NxParser(_in);

		// ********* FIRST RUN *********

		logger.info("Extracting type clusters.");
		logger.info("Mark/Reset supported: " + _in.markSupported());
		// fill up tc-cache
		while (nxp.hasNext()) {
			processNQuadCollectTC(nxp.next());
		}

		// ********* SECOND RUN *********

		_in = _input.getInputStream();
		// reset NxParser for second run
		nxp = new NxParser(_in);

		// reset triple counter for second run
		triple_count = 0;

		logger.info("Extracting schema.");
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
		// TODO Check, if property is subClassOf, subPropertyOf, etc.

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

		Datasource ds = new Datasource(context.toString());
		// property is rdfs:label
		if (SchemaConstants.SNIPPET_PROPERTIES.contains(property.toString())) {
			instance.addSnippet(ds, object.toString());
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

	protected void processNQuadCollectTC(Node[] _ns) throws Exception {
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
		InstanceFull instance = null;
		boolean in_cache = false;

		// check, if the subject is already in the cache, otherwise create a new
		// instance
		if (backward_cache.containsInstance(subject.toString())) {
			// read instance from cache
			instance = (InstanceFull) backward_cache.getInstance(subject
					.toString());
			in_cache = true;
		} else {
			// create new instance
			instance = new InstanceFull(subject.toString());
		}

		// property is rdf:type
		if (property.toString().equals(SchemaConstants.RDF_TYPE)) {
			instance.addType(object.toString());
		}

		// add the instance to the cache
		if (!in_cache) {
			if (!backward_cache.add(instance)) {
				logger.fatal("TC-Cache overflow.");
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
	@Override
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
					// check, whether the instance is contained by the tc_cache
					if (backward_cache.containsInstance(l.getObject()
							.toString())) {
						// creates a new link
						links.add(new Link(l.getProperty(), new Resource(
								backward_cache.getInstance(
										l.getObject().toString())
										.getTypesPostfix())));
					} else {
						// set object to "not resolved"
						links.add(new Link(l.getProperty(), new Resource(
								"notresolved")));
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

}
