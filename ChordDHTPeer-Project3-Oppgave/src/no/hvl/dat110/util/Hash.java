package no.hvl.dat110.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

	private static BigInteger hashint;
	private static byte[] digest;

	public static BigInteger hashOf(String entity) {

		// Task: Hash a given string using MD5 and return the result as a BigInteger.

		// we use MD5 with 128 bits digest

		try {
			// compute the hash of the input 'entity'
			MessageDigest md = MessageDigest.getInstance("MD5");
			digest = md.digest(entity.getBytes());

			// convert the hash into hex format
			String hex = toHex(digest);

			// convert the hex into BigInteger
			hashint = new BigInteger(hex, 16);

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
//		int lenght = digest.length;
		// compute the number of bits = digest length * 8
//		int bits = lenght * 8;

		int bits = bitSize();
		// compute the address size = 2 ^ number of bits

		BigInteger two = BigInteger.valueOf(2);
		BigInteger addressSize = two.pow(bits);
		// return the address size
		return addressSize;
	}

	public static int bitSize() {

		int digestlen = digest.length;

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
