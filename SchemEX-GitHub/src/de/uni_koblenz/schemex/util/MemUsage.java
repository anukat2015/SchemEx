package de.uni_koblenz.schemex.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemUsage {
	
	private static long megabyte = 1024L * 1024L;
	
	/**
	 * Get memory usage of running Java process
	 * @return MemoryUsage object
	 */
	public static MemoryUsage get() {
		MemoryMXBean runtime = ManagementFactory.getMemoryMXBean();
		return runtime.getHeapMemoryUsage();
	}
	
	public static long getUsed() {
		MemoryMXBean runtime = ManagementFactory.getMemoryMXBean();
		return runtime.getHeapMemoryUsage().getUsed() / megabyte;
	}

}
