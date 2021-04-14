package no.hvl.dat110.util;

import java.math.BigInteger;

public class EmmaTest {

	public static void main(String[] args) {


		BigInteger id = BigInteger.valueOf(1124124);
		
		BigInteger lower = BigInteger.valueOf(3);
		
		BigInteger upper = BigInteger.valueOf(2);
		
		System.out.println(Util.computeLogic(id, lower, upper));
		

	}

}
