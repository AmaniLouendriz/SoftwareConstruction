import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

 
public class Main {
	
	public static void main(String[] args) throws InterruptedException {
		
	       
		    int n = 0; 
	        try (Scanner s=new Scanner(System.in)) {
	        	while (true){
	        		System.out.println("Enter the number of Queens :");
	        		n = s.nextInt();
	        		if ( n == 2 || n ==3) {
	        			System.out.println("No Solution possible for "+ n +" Queens. Please enter another number");
	        		}
	        		else
	        			break;
	        	}
	        }

			// Making a group of Threads
			ThreadGroup groupOfThreads = new ThreadGroup("Workers");

			// Initializing a list of threads, number of threads would be number of n
			Thread[] workThreads = new Thread[n];
			// Main doesn't move until a thread finishes and releases semaphore
			// When the main starts the threads, each one of them aquires the semaphore, 
			// and then main would try to aquire a semaphore as well, but would be in a blocking state until one of its child threads 
			// releases the semaphore
			Semaphore s = new Semaphore(n);
			// When a thread finds the result, it makes sure to block all other threads running
			// while it is putting the output into the result array
			// Only one thread should access that array at a time
			Semaphore stopAllTh = new Semaphore(1);

			// Thread that finished stores result here. So we can print it to console
			List<NQueen[]> result = new ArrayList<>();




			// Make each thread part of our group and part of the array of threads
			for (int i = 0;i<n;i++){
				Thread t = new Thread(groupOfThreads,new HillClimbingSearch(n,s,stopAllTh,result));
				workThreads[i] = t;
			}

			long timestamp1 = System.currentTimeMillis();

			// Now that all threads are in the group, let's start them 
			for (int i = 0;i<workThreads.length;i++){
				workThreads[i].start();
			}



		    s.acquire(); 

			// If we are here, then one thread completed. Because a permit got released

			// Then interrup all threads. This won't terminate them now because it just sets isInterrupted to true, 
			// There should be a loop where they check if they are interrupted or not.
			groupOfThreads.interrupt();

			System.out.println("Solution to "+ n +" queens using hill climbing search:");

			// This method is the same as printState with some slight modifications

			//Creating temporary board from the present board
			int[][] tempBoard = new int[n][n];
			NQueen[] res = result.get(0);
			for (int i=0; i<n; i++) {
				//Get the positions of Queen from the Present board and set those positions as 1 in temp board
				tempBoard[res[i].getRow()][res[i].getColumn()]=1;
			}
			System.out.println();
				for (int i=0; i<n; i++) {
					for (int j= 0; j < n; j++) {
						System.out.print(tempBoard[i][j] + " ");
					}
					System.out.println();
				}
			
	        //Printing the solution
	        long timestamp2 = System.currentTimeMillis();
			
			long timeDiff = timestamp2 - timestamp1;
			System.out.println("Execution Time: "+timeDiff+" ms");    
	    }
}