package de.uni_koblenz.schemex.util;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.yars.nx.BooleanLiteral;
import org.semanticweb.yars.nx.DateTimeLiteral;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.NumericLiteral;
import org.semanticweb.yars.nx.Resource;

import de.uni_koblenz.schemex.SchemExConstants;

public class NodeMethods {

	public static boolean isLiteral(Node _node) {
		if (_node instanceof BooleanLiteral) {
			return true;
		} else if (_node instanceof NumericLiteral) {
			return true;
		} else if (_node instanceof DateTimeLiteral) {
			return true;
		} else if (_node instanceof Literal) {
			return true;
		}
		return false;
	}

	public static boolean isLiteral(String _node) {
		return isLiteral(new Resource(_node));
	}

	public static boolean isLiteralURI(String uri) {
		return SchemExConstants.LITERAL_TYPES.contains(uri);
	}
}
