package de.uni_koblenz.schemex.schema;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import de.uni_koblenz.schemex.SchemEx;

public class TripleWriter extends PrintWriter{
	
	/**
	 * Create a triple writer
	 * 
	 * @param _filename Filename / path of the file to be written
	 * @throws IOException 
	 */
	public TripleWriter(String _filename) throws IOException {
		super(new BufferedWriter(new FileWriter(_filename)));
	}
	
	/**
	 * Create a triple writer (appending triples)
	 * 
	 * @param _filename Filename / path of the file to be written
	 * @param _append append triples to existing file
	 * @throws IOException 
	 */
	public TripleWriter(String _filename, boolean _append) throws IOException {
		super(new BufferedWriter(new FileWriter(_filename, _append)));
	}
	
	public void printTriple(String _subject, String _pred, String _object) {
//		if (_subject.indexOf("kanzaki") >= 0) {
//			System.out.println("Uh-Oh  ... : "+ _object);
//		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(_subject);
		buffer.append(' ');
		buffer.append(_pred);
		buffer.append(' ');
		buffer.append(_object);
		buffer.append(" .");
		println(buffer.toString());		
	}
	
	public void printUriTriple(String _subjectUri, String _predUri, String _objectUri) {
		StringBuffer buffer = new StringBuffer();
		buffer.append('<');
		buffer.append(_subjectUri);
		buffer.append("> <");
		buffer.append(_predUri);
		buffer.append("> <");
		buffer.append(_objectUri);
		buffer.append("> .");
		println(buffer.toString());		
	}

	public void printLiteralTriple(String _subjectUri, String _predUri, String _objectLiteral) {
		StringBuffer buffer = new StringBuffer();
		buffer.append('<');
		buffer.append(_subjectUri);
		buffer.append("> <");
		buffer.append(_predUri);
		buffer.append("> \"");
		buffer.append(SchemEx.normalizeLiteral(_objectLiteral));
		buffer.append("\" .");
		println(buffer.toString());		
		
	}


}
