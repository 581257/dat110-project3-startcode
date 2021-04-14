package no.hvl.dat110.util;

/**
 * project 3

 * @author Regine
 *
 */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

	private static BigInteger hashint;

	public static BigInteger hashOf(String entity) {

		// Task: Hash a given string using MD5 and return the result as a BigInteger.

		// we use MD5 with 128 bits digest

		try {
			// compute the hash of the input 'entity'
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(entity.getBytes());
			
			// convert the hash into hex format
			String hex = toHex(digest);
			
			// convert the hex into BigInteger
			hashint = new BigInteger(hex,16);
		
			
			
			

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return the BigInteger
		return hashint;
	}
	
	

	public static BigInteger addressSize() {

		// Task: compute the address size of MD5

		// get the digest length

		// compute the number of bits = digest length * 8

		// compute the address size = 2 ^ number of bits

		// return the address size

		return null;
	}

	public static int bitSize() {

		int digestlen = 0;

		// find the digest length

		return digestlen * 8;
	}

	public static String toHex(byte[] digest) {
		StringBuilder strbuilder = new StringBuilder();
		for (byte b : digest) {
			strbuilder.append(String.format("%02x", b & 0xff));
		}
		return strbuilder.toString();
	}

}
