package de.uni_koblenz.schemex.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper-class for generating MD5-hashs
 * @author Mathias
 *
 */
public class Hash {
	
	/**
	 * Generating MD5 hash values
	 * 
	 * @param _input string to be md5-hashed
	 * @return md5-hashed _input-string
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5(String _input) {
		String hashtext = "";
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(_input.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			hashtext = bigInt.toString(16);
			// fill up with zeros to get a string with length 32
			while(hashtext.length() < 32 ){
			  hashtext = "0"+hashtext;
			}
			
			
		}
		catch (NoSuchAlgorithmException e) {
			hashtext = "MD5 not found";
		}
		return hashtext;
	}

}
