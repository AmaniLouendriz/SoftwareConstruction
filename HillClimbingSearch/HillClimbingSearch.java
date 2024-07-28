//Program to implement Hill Climbing with random restart to solve N-queens problem
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class HillClimbingSearch extends Thread {
    private int n ;
    private int heuristic = 0;
    private int presentHeuristic;
    private NQueen[] finalSolution;
    private List<NQueen[]> result;
    private Semaphore s;
    private Semaphore stopAllThread;

    // result is the list where the final thread will put its NQueen[]
    // Semaphore s is a semaphore used for cooperation synchronization between the threads and their main thread. i.e: 
    // The main thread cannot proceed after starting the threads until one thread finishes the execution.
    // It is initialized by the main thread with (numberOfChildrenThreads) permits.
    
    // When a thread finishes execution (found a solution), it releases one permit using semaphore s. Therefore the main thread
    // is able to proceed and interrupt after all the thread in the threadGroup.
    
    
    // The semaphore stopAllThread is used for competition synchronozation between the different children threads, 
    // when a children thread  finds a solution, no other thread should push its output (in case two children threads got to the final solution at relatively close periods of time)
    // in the shared array "result "until that thread finishes. It's a critical section.
    
    public  HillClimbingSearch (int size,Semaphore s,Semaphore stopAllThread,List<NQueen[]> result) {
        try {
            s.acquire();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    	n = size;	
    	finalSolution = null;
        this.result = result;
        this.s = s;
        this.stopAllThread = stopAllThread;
    }

    @Override

    public void run(){
        runSearch();
    }
    
    public NQueen[] getFinalSolution() {
    	return finalSolution;
    }

    //Method to create a new random board
    public NQueen[] generateBoard() {
        NQueen[] startBoard = new NQueen[n];
        Random rndm = new Random();
        for(int i=0; i<n; i++){
            startBoard[i] = new NQueen(rndm.nextInt(n), i);
        }
        return startBoard;
    }

    //Method to print the Current State
    public  void printState (NQueen[] state) {
        //Creating temporary board from the present board
        int[][] tempBoard = new int[n][n];
        for (int i=0; i<n; i++) {
            //Get the positions of Queen from the Present board and set those positions as 1 in temp board
            tempBoard[state[i].getRow()][state[i].getColumn()]=1;
        }
        System.out.println();
        for (int i=0; i<n; i++) {
            for (int j= 0; j < n; j++) {
                System.out.print(tempBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Method to find Heuristics of a state
    public  int findHeuristic (NQueen[] state) {
        int heuristic = 0;
        for (int i = 0; i< state.length; i++) {
            for (int j=i+1; j<state.length; j++ ) {
                if (state[i].ifConflict(state[j])) {
                    heuristic++;
                }
            }
        }
        return heuristic;
    }

    // Method to get the next board with lower heuristic
    public NQueen[] nextBoard (NQueen[] presentBoard) {
        NQueen[] nextBoard = new NQueen[n];
        NQueen[] tmpBoard = new NQueen[n];
        int presentHeuristic = findHeuristic(presentBoard);
        int bestHeuristic = presentHeuristic;
        int tempH;

        for (int i=0; i<n; i++) {
            //  Copy present board as best board and temp board
            nextBoard[i] = new NQueen(presentBoard[i].getRow(), presentBoard[i].getColumn());
            tmpBoard[i] = nextBoard[i];
        }
        //  Iterate each column
        for (int i=0; i<n; i++) {
            if (i>0)
                tmpBoard[i-1] = new NQueen (presentBoard[i-1].getRow(), presentBoard[i-1].getColumn());
            tmpBoard[i] = new NQueen (0, tmpBoard[i].getColumn());
            //  Iterate each row
            for (int j=0; j<n; j++) {
                //Get the heuristic
                tempH = findHeuristic(tmpBoard);
                //Check if temp board better than best board
                if (tempH < bestHeuristic) {
                    bestHeuristic = tempH;
                    //  Copy the temp board as best board
                    for (int k=0; k<n; k++) {
                        nextBoard[k] = new NQueen(tmpBoard[k].getRow(), tmpBoard[k].getColumn());
                    }
                }
                //Move the queen
                if (tmpBoard[i].getRow()!=n-1)
                    tmpBoard[i].move();
            }
        }
        //Check whether the present bord and the best board found have same heuristic
        //Then randomly generate new board and assign it to best board
        if (bestHeuristic == presentHeuristic) {
            nextBoard = generateBoard();
            heuristic = findHeuristic(nextBoard);
        } else
            heuristic = bestHeuristic;
        return nextBoard;
    }
    
    public void runSearch(){
        if (!Thread.currentThread().isInterrupted()) {
            NQueen[] presentBoard = generateBoard();
            presentHeuristic = findHeuristic(presentBoard);
        
            // test if the present board is the solution board

            // Stay in the loop while presentHeuristic != 0 and currentThread.isInterrupted() is false.
            while (presentHeuristic != 0 && !Thread.currentThread().isInterrupted() ) {
                //  Get the next board
                // printState(presentBoard);
                presentBoard = nextBoard(presentBoard);
                presentHeuristic  = heuristic;
            }
        
            finalSolution = presentBoard;
            if (Thread.currentThread().isInterrupted()){
                return;
            }
        }
        if (this.getFinalSolution()!= null && !Thread.currentThread().isInterrupted()){
            try {
                    try{
                        stopAllThread.acquire();
                    }catch(InterruptedException e){
                        return;
                    }
                    if (!Thread.currentThread().isInterrupted()){  
                        // System.out.println("I am thread "+Thread.currentThread().getName() +" found a solution first");
                        NQueen[] res = this.getFinalSolution();
                        // System.out.print("length in that res"+res.length);
                        result.add(res);  
                        s.release();
                        // System.out.println("I am thread "+Thread.currentThread().getName() +" here");
                        stopAllThread.release();   
                    }else{
                        return;
                    }
                } catch (Exception e) {
                e.printStackTrace();
                }                
        }    	
	}
}