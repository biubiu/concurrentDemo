package com.shawn.concurrent.demo.basic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.Thread.State;



public class BasicThreadTest {

    public static void main(String[] args) {
        //RunSimpleThread();
    	RunWithStateMonitoring();
    	
    }

    public  static void RunSimpleThread(){
        for (int i = 0; i < 10; i++) {
             Thread d= new Thread(new Calculator(i));        	 
             d.start();
        }
    }
    
    public static  void RunWithStateMonitoring() {
        Thread threads[] = new Thread[10];
        Thread.State status[] = new Thread.State[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Calculator(i));
            if((i%2) == 0){
                threads[i].setPriority(Thread.MAX_PRIORITY);
            }else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("T " + i);
        }
        printThreadInfo(threads,status);
    }
    
    private static void printThreadInfo(Thread[] threads,Thread.State[] status){

        
        try {
        	//make file to record the states of the threads
        	FileWriter fileWriter = new FileWriter("metadata/log.txt");
        	
        	PrintWriter pw= new PrintWriter(fileWriter);
             for (int i = 0; i < 10; i++) {
                 pw.println("Main: Status of Thread" + i + ":"+threads[i].getState());
                 status[i] = threads[i].getState();
             }
             for (int i = 0; i < 10; i++) {
                 threads[i].start();            
             }
             //record thread state until all threads terminates
             boolean finish = false;
             while (!finish) {
                 for(int i=0; i<10;i++){
                     if(threads[i].getState()!=status[i]){
                         writeThreadInfo(pw,threads[i],status[i]);
                         status[i] = threads[i].getState();
                     }
                 }
                 finish = true;
                 for (int i = 0; i < 10; i++) {
                     finish = finish&&(threads[i].getState() == State.TERMINATED);
                }
             }
             pw.close();
        } catch (IOException e) {
			e.printStackTrace();
		}    
    }

    //record the threads state shifting
    private static void writeThreadInfo(PrintWriter pw,Thread t , State state){
        //pw.printf("Main: Id %d - %s\n",t.getId(),t.getName());
        pw.printf("Main: ------- %s\n",t.getName());
        pw.printf("Main:Priority: %d \n",t.getPriority());
        pw.printf("Main: Old State: %s\n",state);
        pw.printf("Main:New State: %s \n",t.getState());
        pw.printf("Main:**************************************************\n");
    }
}


//class Calculator implements Runnable{
class Calculator extends Thread{
    private int number;
    public Calculator(int number) {
        this.number = number;        
    }

    public void run() {
        for (int i = 0; i < 10; i++) {        	
            System.out.printf("%s: %d*%d = %d \n",Thread.currentThread().getName(),number,i,i*number);
        }
    }

}