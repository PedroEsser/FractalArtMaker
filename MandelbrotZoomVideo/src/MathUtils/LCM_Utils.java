package MathUtils;

import java.util.Iterator;

public class LCM_Utils {

	public static long LCMofFirstNNumbers(int n) {
		int sqrt = (int)(Math.sqrt(n));
		Iterator<Long> primes = new PrimeList(n).iterator();
		long p = primes.next();
		long lcm = 1;
		while(p <= sqrt) {
			lcm *= highestPowerLessThan(p, n);
			p = primes.next();
		}
		lcm *= p;
		while(p <= n && primes.hasNext()) {
			p = primes.next();
			lcm *= p;
		}
			
		return lcm;
	}
	
	public static int highestPowerLessThan(long b, int n) {
		int aux = 1;
		while(aux*b <= n) 
			aux *= b;
		return aux;
	}
	
}
