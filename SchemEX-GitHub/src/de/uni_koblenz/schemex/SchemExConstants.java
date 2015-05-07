package de.uni_koblenz.schemex;

import java.util.HashSet;
import java.util.Set;

public class SchemExConstants {
	public final static String MAIN_LOG_FILE = "logs/schemex.log";
	
	public static final Set<String> LITERAL_TYPES;
	static {
		LITERAL_TYPES = new HashSet<String>();
		LITERAL_TYPES.add("http://www.w3.org/2001/XMLSchema#boolean");
		LITERAL_TYPES.add("http://www.w3.org/2001/XMLSchema#dateTime");
		LITERAL_TYPES.add("http://www.w3.org/2001/XMLSchema#integer");
		LITERAL_TYPES.add("http://www.w3.org/2001/XMLSchema#byte");
		LITERAL_TYPES.add("http://www.w3.org/2001/XMLSchema#short");
		LITERAL_TYPES.add("http://www.w3.org/2001/XMLSchema#long");
		LITERAL_TYPES.add("http://www.w3.org/2001/XMLSchema#float");
		LITERAL_TYPES.add("http://www.w3.org/2001/XMLSchema#double");
		LITERAL_TYPES.add("http://www.w3.org/2001/XMLSchema#string");
		LITERAL_TYPES.add("http://www.w3.org/2000/01/rdf-schema#Literal");
	}
}
