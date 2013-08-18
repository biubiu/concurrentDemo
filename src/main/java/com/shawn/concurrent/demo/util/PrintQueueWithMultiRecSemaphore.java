package com.shawn.concurrent.demo.util;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintQueueWithMultiRecSemaphore {
     public static void main(String[] args){
         PrintQueueWithMultiRec printQueueWithMultiRec=new PrintQueueWithMultiRec();
            Thread thread[]=new Thread[10];
            for (int i=0; i<10; i++){
                thread[i]=new Thread(new JobWithMultiRec(printQueueWithMultiRec),"Thread"+i);
            }
            for (int i=0; i<10; i++){
                thread[i].start();
            }
        }
}


class JobWithMultiRec implements Runnable{
    private PrintQueueWithMultiRec printQueueWithMultiRec;

    public JobWithMultiRec(PrintQueueWithMultiRec printQueueWithMultiRec){
        this.printQueueWithMultiRec = printQueueWithMultiRec;
    }

    public void run() {
        System.out.printf("%s: Going to print a job\n",Thread.currentThread().getName());
        printQueueWithMultiRec.printJob(new Object());
        System.out.printf("%s: The document has been printed\n",Thread.currentThread().getName());
    }
  }

class PrintQueueWithMultiRec{
    private boolean freePrinters[];
    private Lock lockPrinters;
    private Semaphore semaphore;
    public PrintQueueWithMultiRec() {
        semaphore = new Semaphore(3);
        freePrinters = new boolean[3];
        for (int i = 0; i < freePrinters.length; i++) {
            freePrinters[i] = true;
        }
        lockPrinters = new ReentrantLock();
    }

    public void printJob(Object document){
        try {
            semaphore.acquire();
            int assignedPrinter = getPrinter();
            long duration = (long) (Math.random()*10);
            System.out.printf("%s: printQueue: Printing a job in Printer %d during %d seconds \n", Thread.currentThread().getName(),assignedPrinter,duration);
            TimeUnit.SECONDS.sleep(duration);
            freePrinters[assignedPrinter] = true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            semaphore.release();
        }
    }

    private int getPrinter(){
        int ret = -1;
        try {
            lockPrinters.lock();
            for (int i = 0; i < freePrinters.length; i++) {
                if (freePrinters[i]) {
                    ret =i;
                    freePrinters[i] = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            lockPrinters.unlock();
        }
        return ret;
    }
}
