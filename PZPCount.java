package pzpcount;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

    public class PZPCount {

    	private static boolean isPZP(final int n) {
            if(n < 2 || (n % 2 == 0 && n != 2)) {
            	return false;
            }
            int reverse = 0;
            int r = n;
            while(r != 0) {
            	reverse = reverse * 10 + r % 10;
            	r= r / 10;
            }
            return n == reverse && isPrime(n);
    	}
    	
    	private static boolean isPrime(final int n) {
    		if (n < 2) {
    			return false;
    		}
    		for(int i = 2; i*i <= n; i++) {
    			if(n % i == 0) {
    				return false;
    			}
    		}
    		return true;
    	}
        
        public static int seqCountPZP(final int from, final int to) {

            int counter = 0;
            for(int n = from; n <= to; n++) {
                if(isPZP(n))
                    counter++;
            }
            return counter;
        }

        public static int parCountPZP(final int from, final int to, final int nrParallelThreads) {
            ExecutorService executor = Executors.newFixedThreadPool(nrParallelThreads);

            final List<Future<Integer>> promisedResults = new ArrayList<Future<Integer>>();
            int amount = (to - from + 1) / nrParallelThreads;
            int remainder = (to - from + 1) % nrParallelThreads;
            int s = from;

            for(int i = 0; i < nrParallelThreads; i++) {
                int e = s + amount - 1;
                if(remainder > 0) {
                    e++;
                }
                int finalStart = s;
                int finalEnding = e;
                promisedResults.add(executor.submit(() -> seqCountPZP(finalStart, finalEnding)));
                s = e + 1;
                remainder--;
            }
            int counter = 0;
            for(Future<Integer> result : promisedResults) {
                try {
                    counter += result.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
            return counter;
        }

        public static void main(String[] args) {
            int from = 1;
            int to = 100000;
            int nrParallelThreads = 4;
            long start;
            long end;

            start = System.currentTimeMillis();
            int seqCount = seqCountPZP(from, to);
            end = System.currentTimeMillis();


            System.out.println("Seq Count: " + seqCount + " Palindromic primes in " + (end - start) + " milliseconds.");

            start = System.currentTimeMillis();
            int parCount = parCountPZP(from, to, nrParallelThreads);
            end = System.currentTimeMillis();

            System.out.println("Par Count: (" + nrParallelThreads + " Threads): " + parCount + " Palindromic primes in " + (end - start) + " milliseconds.");

        }

    }