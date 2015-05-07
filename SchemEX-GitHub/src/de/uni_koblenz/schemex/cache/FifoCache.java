package de.uni_koblenz.schemex.cache;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * FIFO-Cache
 * 
 * @author Mathias
 *
 */
public class FifoCache implements InstanceCache {
	
	/**
	 * logger
	 */
	private static  Logger logger = LogManager.getRootLogger();
	
	/**
	 * Map that contains the instances; String/key = URI
	 */
	protected HashMap<String, Instance> map;
	/**
	 * Queue that controls the FIFO mechanism.
	 */
	protected BlockingQueue<String> queue;
	/**
	 * Size of the cache - number of max instances
	 */
	protected int cachesize = 0;
		
	/**
	 * Creates a new FIFO-Cache with a given cachesize.
	 * 
	 * @param _cachesize Number of instances that the Cache can hold.
	 */
	public FifoCache(int _cachesize) {
		cachesize = _cachesize;
		// create queue
		queue = new ArrayBlockingQueue<String>(cachesize);
		// create hashmap 
		map = new HashMap<String, Instance>(calculateHashMapSize(cachesize));
		logger.info("FIFO-Cache created. Cachesize: " + cachesize);
	}
	
	/**
	 * Creates a new FIFO-Cache with standard cachesize defined by CacheConstants
	 */
	public FifoCache() {
		cachesize = CacheConstants.STD_CACHESIZE;
		// create queue
		queue = new ArrayBlockingQueue<String>(cachesize);
		// create hashmap 
		map = new HashMap<String, Instance>(calculateHashMapSize(cachesize));
		logger.info("FIFO-Cache created. Cachesize: " + cachesize);

	}

	/**
	 * @see de.uni_koblenz.schemex.cache.InstanceCache#add(de.uni_koblenz.schemex.cache.Instance)
	 */
	@Override
	public boolean add(Instance _instance) {
		if (queue.remainingCapacity() == 0) {
			return false;
		}
		else {
			// add the instance URI to the FIFO queue
			queue.add(_instance.getInstanceURI());
			// add the instance to the hashmap
			map.put(_instance.getInstanceURI(), _instance);
			// set new array pointers
			return true;
		}
		
	}

	/**
	 * @see de.uni_koblenz.schemex.cache.InstanceCache#containsInstance(java.lang.String)
	 */
	@Override
	public boolean containsInstance(String _instance_uri) {
		return map.containsKey(_instance_uri);
	}
	
	/**
	 * @see de.uni_koblenz.schemex.cache.InstanceCache#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	/**
	 * @see de.uni_koblenz.schemex.cache.InstanceCache#size()
	 */
	@Override
	public int size() {
		return queue.size();
	}
	
	/**
	 * @see de.uni_koblenz.schemex.cache.InstanceCache#maxSize()
	 */
	public int maxSize() {
		return cachesize;
	}
	
	/**
	 * @see de.uni_koblenz.schemex.cache.InstanceCache#getInstance(java.lang.String)
	 */
	@Override
	public Instance getInstance(String _instance_uri) {
		if (containsInstance(_instance_uri)) {
			return map.get(_instance_uri);
		}
		throw new IllegalStateException("Instance is not contained by the cache.");
	}

	/**
	 * @throws Exception 
	 * @see de.uni_koblenz.schemex.cache.InstanceCache#remove()
	 */
	@Override
	public Instance remove() {
		try {
			String instance_uri = queue.peek();
			queue.remove();
			return map.remove(instance_uri);
		}
		catch (NoSuchElementException e) {
			System.out.println("Cannot remove element - cache is empty:" + e.getMessage());
			return null;
		}
			
	}
	
	/**
	 * Calculate optimal hashmap size values (prime numbers)
	 * 
	 * @param _cachesize size of the cache
	 * @return optimal value for initial hashmap size
	 */
	protected int calculateHashMapSize(int _cachesize) {
		int double_cachsize = _cachesize * 2;
		if (double_cachsize < 11) return 11;
		if (double_cachsize < 17) return 17;
		if (double_cachsize < 37) return 37;
		if (double_cachsize < 67) return 67;
		if (double_cachsize < 131) return 131;
		if (double_cachsize < 257) return 257;
		if (double_cachsize < 521) return 521;
		if (double_cachsize < 1031) return 1031;
		if (double_cachsize < 2053) return 2053;
		if (double_cachsize < 4099) return 4099;
		if (double_cachsize < 8209) return 8209;
		if (double_cachsize < 16411) return 16411;
		if (double_cachsize < 32771) return 32771;
		if (double_cachsize < 65537) return 65537;
		if (double_cachsize < 131101) return 131101;
		if (double_cachsize < 262147) return 262147;
		if (double_cachsize < 524309) return 524309;
		if (double_cachsize < 1048583) return 1048583;
		if (double_cachsize < 2097169) return 2097169;
		if (double_cachsize < 4194319) return 4194319;
		return 8388617;
	}
	
	
	// simple test
//    public static void main(String[] args) throws Exception {
//        InstanceCache cache = new FifoCache(5);
//        System.out.println(cache.maxSize());
//        cache.add(new Instance("a"));
//        System.out.println(cache.size());
//        cache.add(new Instance("b"));
//        System.out.println(cache.size());
//        cache.add(new Instance("c"));
//        System.out.println(cache.size());
//        cache.add(new Instance("d"));
//        System.out.println(cache.size());
//        cache.add(new Instance("e"));
//        System.out.println(cache.size());
//        if (!cache.add(new Instance("f"))) {
//        	System.out.println(cache.remove().getInstance_uri());
//        	System.out.println(cache.size());
//        	cache.add(new Instance("f"));
//        	System.out.println(cache.size());
//        }
//        
//        while (!cache.isEmpty()) {
//        	System.out.println(cache.remove().getInstance_uri());
//        }
//    }

}
