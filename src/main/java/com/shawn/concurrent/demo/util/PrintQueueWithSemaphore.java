package com.shawn.concurrent.demo.util;

import java.util.concurrent.Semaphore;

public class PrintQueueWithSemaphore {
    public static void main(String[] args){
        PrintQueueV printQueueV=new PrintQueueV();
        Thread thread[]=new Thread[10];
        for (int i=0; i<10; i++){
            thread[i]=new Thread(new JobV(printQueueV),"Thread"+i);
        }
        for (int i=0; i<10; i++){
            thread[i].start();
        }
    }

}


class JobV implements Runnable{
    private PrintQueueV printQueueV;

    public JobV(PrintQueueV printQueueV){
        this.printQueueV = printQueueV;
    }

    public void run() {
        System.out.printf("%s: Going to print a job\n",Thread.currentThread().getName());
        printQueueV.printJob(new Object());
        System.out.printf("%s: The document has been printed\n",Thread.currentThread().getName());
    }
  }

class PrintQueueV{
    private final Semaphore semaphore;

    public PrintQueueV(){
        this.semaphore = new Semaphore(1);
    }

    public void printJob(Object document){
        try {
            semaphore.acquire();
            long duration=(long)(Math.random()*10);
            System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n",Thread.currentThread().getName(),duration);
            Thread.sleep(duration);

        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            semaphore.release();
        }
    }
}


