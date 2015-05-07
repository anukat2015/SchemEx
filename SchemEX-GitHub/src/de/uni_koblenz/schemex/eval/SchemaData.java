package de.uni_koblenz.schemex.eval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.parser.ParseException;

import de.uni_koblenz.schemex.schema.SchemaBTC2012Constants;

public class SchemaData {

	private HashMap<String, HashMap<String,Integer>> eqcDSMap = new HashMap<String, HashMap<String,Integer>>();
	private HashMap<String, HashSet<String>> eqcPropertyMap = new HashMap<String, HashSet<String>>();
	private HashMap<String, String> eqcTargetMap = new HashMap<String, String>();
	private HashMap<String, HashSet<String>> tcEqcMap = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> tcClassMap = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> pcEqcMap = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> classTcMap = new HashMap<String, HashSet<String>>();
	
	public static SchemaData parseSchemaFile(File f) throws IOException {
		SchemaData result = new SchemaData();
		FileInputStream fin = new FileInputStream(f);
		NxParser nxparser = null;
//		try {
			nxparser = new NxParser(fin);
//		} catch (ParseException pe) {
//			pe.printStackTrace();
//			return result;
//		}
		int cnt = 0;
		Node[] ntriple;
		HashMap<String, HashSet<String>> eqcDSLinkMap = new HashMap<String, HashSet<String>>();
		HashMap<String, String> dsLinkDSMap = new HashMap<String, String>();
		HashMap<String, Integer> dsLinkSizeMap = new HashMap<String, Integer>();
		while (nxparser.hasNext()) {
			cnt++;
			if (cnt % 100000 == 0) {
				System.out.println("Parsing schema triple " + cnt);
			}
			// try {
			ntriple = nxparser.next();

			String subject = ntriple[0].toString();
			if (subject.startsWith(
					SchemaBTC2012Constants.TC_URI_PREFIX)) {
				String tc = subject.toString().substring(SchemaBTC2012Constants.TC_URI_PREFIX.length());
//				System.out.println(tc);
				String property = ntriple[1].toString(); 
				if (property.startsWith(
						SchemaBTC2012Constants.TC_TO_EQC_PREDICATE_URI)) {
					String eqc = ntriple[2].toString().substring(SchemaBTC2012Constants.EQC_URI_PREFIX.length());
					if (! result.tcEqcMap.containsKey(tc)) {
						result.tcEqcMap.put(tc, new HashSet<String>());
					}
					result.tcEqcMap.get(tc).add(eqc);
				} else if (property.startsWith(
						SchemaBTC2012Constants.RDF_TYPE)) {
					// ignore the rdf:type
				} else if (property.startsWith(
						SchemaBTC2012Constants.TC_TO_CLASS_PREDICATE_URI)) {
					String type = ntriple[2].toString();
					if (! result.tcClassMap.containsKey(tc)) {
						result.tcClassMap.put(tc, new HashSet<String>());
					}
					result.tcClassMap.get(tc).add(type);
				}
			} else if (subject.startsWith(
					SchemaBTC2012Constants.EQC_URI_PREFIX)) {
				String eqc = subject.toString().substring(SchemaBTC2012Constants.EQC_URI_PREFIX.length());
				if (! result.eqcPropertyMap.containsKey(eqc)) {
					result.eqcPropertyMap.put(eqc, new HashSet<String>());
				}
				if (! eqcDSLinkMap.containsKey(eqc)) {
					eqcDSLinkMap.put(eqc, new HashSet<String>());
				}
				String property = ntriple[1].toString(); 
				if (property.startsWith(
						SchemaBTC2012Constants.EQC_TO_DS_LINK_PREDICATE)) {
					String dsLink = ntriple[2].toString().substring(SchemaBTC2012Constants.LINKSET_URI_PREFIX.length());
					eqcDSLinkMap.get(eqc).add(dsLink);
				} else if (property.startsWith(
						SchemaBTC2012Constants.TRIPLE_PER_EQC_PER_DS_PROPERTY)) {
					// ignore the entity counts
				} else if (property.startsWith(
						SchemaBTC2012Constants.RDF_TYPE)) {
					// ignore the rdf:type
				} else if (ntriple[2].toString().startsWith(SchemaBTC2012Constants.TC_URI_PREFIX)) {
					// link to TC
					String tc = ntriple[2].toString().substring(SchemaBTC2012Constants.TC_URI_PREFIX.length());
					result.eqcPropertyMap.get(eqc).add(property);
					result.eqcTargetMap.put(eqc,tc);
				}
			} else if (subject.startsWith(SchemaBTC2012Constants.LINKSET_URI_PREFIX)) {
				String dsLink = subject.toString().substring(SchemaBTC2012Constants.LINKSET_URI_PREFIX.length());
				String property = ntriple[1].toString(); 
				if (property.startsWith(
						SchemaBTC2012Constants.EQC_TO_DS_LINK_TO_DS_PREDICATE)) {
					String ds = ntriple[2].toString();
					dsLinkDSMap.put(dsLink,ds);
				} else if (property.startsWith(
						SchemaBTC2012Constants.INSTANCE_COUNT_PREDICATE_URI)) {
					int entities = Integer.parseInt(ntriple[2].toString());
					if (dsLinkSizeMap.containsKey(dsLink)) {
						// LinkSet previously seen -- add the previous entity count
						entities += dsLinkSizeMap.get(dsLink);
					}
					dsLinkSizeMap.put(dsLink,entities);
				}
				
			}
		}
		// Post processing:
		// filling in data sets and entitiy count to EQC
		for (String eqc: eqcDSLinkMap.keySet()) {
			HashMap<String,Integer> dsMap = new HashMap<String, Integer>();
			HashSet<String> linksets = eqcDSLinkMap.get(eqc);
			for (String ls : linksets) {
				String ds = dsLinkDSMap.get(ls);
				int entities = dsLinkSizeMap.get(ls);
				dsMap.put(ds, entities);
			}
			result.eqcDSMap.put(eqc, dsMap);
		}
		// Computing pcs and map
		for (String eqc: result.eqcPropertyMap.keySet()) {
			String pcCode = SchemaData.encodePropertyCluster(result.eqcPropertyMap.get(eqc));
			if (! result.pcEqcMap.containsKey(pcCode)) {
				result.pcEqcMap.put(pcCode, new HashSet<String>());
			}
			result.pcEqcMap.get(pcCode).add(eqc);
		}
		// Computing map of types
		for (String tc: result.tcClassMap.keySet()) {
			HashSet<String> types = result.tcClassMap.get(tc);
			for (String type : types) {
				if (! result.classTcMap.containsKey(type)) {
					result.classTcMap.put(type, new HashSet<String>());
				}
				result.classTcMap.get(type).add(tc);
			}
		}
		return result;
	}

	public static  String encodePropertyCluster(Set<String> properties) {
		StringBuffer buffer = new StringBuffer();
		for (String prop : properties) {
			buffer.append(prop);
			buffer.append(' ');
		}
		return buffer.toString();
	}
	
	public EvaluationResult compareToGoldStandard(SchemaData gold) {
		EvaluationResult result = new EvaluationResult();
		// TC count
		System.out.print("Counting TCs ... ");
		result.tcCount[EvaluationResult.OTHER] = this.tcClassMap.keySet().size();
		System.out.print("Me ... ");
		result.tcCount[EvaluationResult.GOLD] = gold.tcClassMap.keySet().size();
		System.out.println("Gold");
		// EQC count
		System.out.print("Counting EQCs ... ");
		result.eqcCount[EvaluationResult.OTHER] = this.eqcTargetMap.keySet().size();
		System.out.print("Me ... ");
		result.eqcCount[EvaluationResult.GOLD] = gold.eqcTargetMap.keySet().size();
		System.out.println("Gold");
		// PC count
		System.out.print("Counting PCs ... ");
		result.pcCount[EvaluationResult.OTHER] = this.pcEqcMap.keySet().size();
		System.out.print("Me ... ");
		result.pcCount[EvaluationResult.GOLD] = gold.pcEqcMap.keySet().size();
		System.out.println("Gold");
		// Type count
		System.out.print("Counting types ... ");
		result.types[EvaluationResult.OTHER] = this.typeCount();
		System.out.print("Me ... ");
		result.types[EvaluationResult.GOLD] = gold.typeCount();
		System.out.println("Gold");
		// Property count
		System.out.print("Counting properties ... ");
		result.properties[EvaluationResult.OTHER] = this.propertyCount();
		System.out.print("Me ... ");
		result.properties[EvaluationResult.GOLD] = gold.propertyCount();
		System.out.println("Gold");
		System.out.println("Computing Type queries");
		for (String type: gold.classTcMap.keySet()) {
			HashSet<String> goldDs = gold.getDatasetsforType(type);
			int goldSize = goldDs.size();
			HashSet<String> meDs = this.getDatasetsforType(type);
			int meSize = meDs.size();
			goldDs.retainAll(meDs);
			int bothSize = goldDs.size();
			result.addTypeQuery(goldSize, meSize, bothSize);
		}
		System.out.println("Computing TC queries");
		for (String tc: gold.tcClassMap.keySet()) {
			HashSet<String> goldDs = gold.getDatasetsforTypeCluster(tc);
			int goldSize = goldDs.size();
			HashSet<String> meDs = this.getDatasetsforTypeCluster(tc);
			int meSize = meDs.size();
			goldDs.retainAll(meDs);
			int bothSize = goldDs.size();
			result.addTcQuery(goldSize, meSize, bothSize);
		}
		System.out.println("Computing TCS queries");
		for (String tc: gold.tcClassMap.keySet()) {
			HashSet<String> tcs = gold.tcClassMap.get(tc);
			HashSet<String> goldDs = gold.getDatasetsforSuperTypeCluster(tcs);
			int goldSize = goldDs.size();
			HashSet<String> meDs = this.getDatasetsforSuperTypeCluster(tcs);
			int meSize = meDs.size();
			goldDs.retainAll(meDs);
			int bothSize = goldDs.size();
			result.addTcsQuery(goldSize, meSize, bothSize);
		}
		System.out.println("Computing PC queries");
		for (String pc: gold.pcEqcMap.keySet()) {
			HashSet<String> goldDs = gold.getDatasetsforPropertySet(pc);
			int goldSize = goldDs.size();
			HashSet<String> meDs = this.getDatasetsforPropertySet(pc);
			int meSize = meDs.size();
			goldDs.retainAll(meDs);
			int bothSize = goldDs.size();
			result.addPcQuery(goldSize, meSize, bothSize);
		}
		System.out.println("Computing EQC queries");
		for (String eqc: gold.eqcDSMap.keySet()) {
			HashSet<String> goldDs = new HashSet<String>();
			goldDs.addAll(gold.eqcDSMap.get(eqc).keySet());
			int goldSize = goldDs.size();
			HashSet<String> meDs = new HashSet<String>();
			if (this.eqcDSMap.containsKey(eqc)) {
				meDs.addAll(this.eqcDSMap.get(eqc).keySet());
			}
			int meSize = meDs.size();
			goldDs.retainAll(meDs);
			int bothSize = goldDs.size();
			result.addEqcQuery(goldSize, meSize, bothSize);
		}
		return result;
	}
	
	public int typeCount() {
		return this.classTcMap.keySet().size();
	}
	
	public int propertyCount() {
		HashSet<String> properties = new HashSet<String>();
		for (HashSet<String> eqcProps : this.eqcPropertyMap.values()) {
			properties.addAll(eqcProps);
		}
		return properties.size();
	}

	public HashSet<String> getDatasetsforType(String type) {
		HashSet<String> result = new HashSet<String>();
		if (this.classTcMap.containsKey(type)) {
			HashSet<String> tcs = this.classTcMap.get(type);
			for (String tc : tcs) {
				result.addAll(this.getDatasetsforTypeCluster(tc));
			}
		}
		return result;
	}
	
	public HashSet<String> getDatasetsforTypeCluster(String tc) {
		HashSet<String> result = new HashSet<String>();
		if (this.tcEqcMap.containsKey(tc)) {
			HashSet<String> eqcs = this.tcEqcMap.get(tc);
			for (String eqc : eqcs) {
				result.addAll(this.eqcDSMap.get(eqc).keySet());
			}
		}
		return result;
	}
	
	public HashSet<String> getDatasetsforPropertySet(String pc) {
		HashSet<String> result = new HashSet<String>();
		if (this.pcEqcMap.containsKey(pc)) {
			HashSet<String> eqcs = this.pcEqcMap.get(pc);
			for (String eqc : eqcs) {
				result.addAll(this.eqcDSMap.get(eqc).keySet());
			}
		}
		return result;
	}
	
	public HashSet<String> getDatasetsforSuperTypeCluster(HashSet<String> tcs) {
		HashSet<String> result = new HashSet<String>();
		HashSet<String> intersection = new HashSet<String>();
		boolean first = true;
		for(String type: tcs) {
			if (this.classTcMap.containsKey(type)) {
				HashSet<String> typeClusters = this.classTcMap.get(type);
				if (first) {
					intersection.addAll(typeClusters);
				} else {
					intersection.retainAll(typeClusters);
				}
			}
			first = false;
		}
		for (String typeCluster: intersection) {
			result.addAll(this.getDatasetsforTypeCluster(typeCluster));
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		String filename = "schema.nt";
		String gold = "gold.nt";
		if (args.length >= 1) {
			filename = args[0];
		}
		if (args.length >= 2) {
			gold = args[1];
		}
		System.out.println("Reading Schema");
		SchemaData sd = SchemaData.parseSchemaFile(new File(filename));
		System.out.println("Reading Gold Standard");
		SchemaData sdGold = SchemaData.parseSchemaFile(new File(gold));
		System.out.println("Comparing ...");
		EvaluationResult evaRes = sd.compareToGoldStandard(sdGold);
		System.out.println(evaRes);
	}

}
