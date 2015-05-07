/**
 * 
 */
package de.uni_koblenz.schemex.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Mathias
 *
 */
public class ValueArray {
	
	ArrayList<Double> values;
	
	public ValueArray() {
		values = new ArrayList<Double>();
	}
	
	public boolean addValue(Double _value) {
		return values.add(_value);
	}
	
	public double average() {
		if (size() == 0) {
			return 0.0;
		} else {
			Iterator<Double> iter = values.iterator();
			double x = 0.0;
			while (iter.hasNext()) {
				x += iter.next().doubleValue();
			}
			return x / values.size();
		}
	}
	
	public double median() {
		if (size() == 0) {
			return 0.0;
		} else {
			Collections.sort(values);
			return values.get(Math.round(values.size()/2));
		}
	}
	
	public double stddev() {
		if (size() == 0) {
			return 0.0;
		} else {
			Iterator<Double> iter = values.iterator();
			double sum = 0.0;
			double ave = average();
			while (iter.hasNext()) {
				sum += Math.pow(iter.next().doubleValue() - ave, 2) ;
			}
			return Math.sqrt(sum/(size()-1));
		}
	}
	
	public double min() {
		if (size() == 0) {
			return 0.0;
		} else {
			Iterator<Double> iter = values.iterator();
			double min = Double.MAX_VALUE;
			while (iter.hasNext()) {
				double val = iter.next().doubleValue();
				if (val < min) {
					min = val;
				}
			}
			return min;
		}
	}
	
	public double max() {
		if (size() == 0) {
			return 0.0;
		} else {
			Iterator<Double> iter = values.iterator();
			double max = Double.MIN_VALUE;
			while (iter.hasNext()) {
				double val = iter.next().doubleValue();
				if (val > max) {
					max = val;
				}
			}
			return max;
		}
	}
	
	public int size() {
		return values.size();
	}
	
	public void toFile(String _filename) {
		try {
			File file = new File(_filename);
			boolean exists = file.exists();
			if (!exists) {
				file.createNewFile();
			}
			// Create file
			FileWriter fstream = new FileWriter(_filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Count: " + size() + "\r\n");
			out.write("Average: " + average() + "\r\n");
			out.write("Median: " + median() + "\r\n");
			out.write("Min: " + min() + "\r\n");
			out.write("Max: " + max() + "\r\n");
			out.write("StdDev: " + stddev() + "\r\n");
			out.write("\r\n");
			// values
			Iterator<Double> iter = values.iterator();
			while (iter.hasNext()) {
				out.write(iter.next().doubleValue() + "\r\n");
			}
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage() + " " + _filename);
		}
	}

}
