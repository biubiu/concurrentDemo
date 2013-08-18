package com.shawn.concurrent.demo.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class PrintQueueWithReentrantLock {
    public static void main(String[] args) {
        PrintQueue printQueue = new PrintQueue();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Job(printQueue), "Thread " + i);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
    }
}

class Job implements Runnable {
    private PrintQueue printQueue;

    public Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    public void run() {
        System.out.printf("%s: Going to print a document\n", Thread.currentThread().getName());
        printQueue.printJob(new Object());
       // printQueue.printJobWithTimeDuration(new Object());
        System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());
    }

}

class PrintQueue {
    // private final Lock queueLock = new ReentrantLock();
    private final Lock queueLock = new ReentrantLock(true);

    public void printJob(Object document) {
        if (queueLock.tryLock()) {
            /**
             * alternatively:queueLock.lock();
             *
             */

            try {
                Long duration = (long) (Math.random() * 100);
                System.out.println(Thread.currentThread().getName() + ":PrintQueue: Printing a Job during " + (duration / 1000) + " seconds");
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*
         * alternative: finally { queueLock.unlock(); } }
         */
    }

    public void printJobWithTimeDuration(Object document) {
        for (int i = 0; i < 2; i++) {
            queueLock.lock();
            try {
                Long duration = (long) (Math.random() * 5000);
                System.out.println(Thread.currentThread().getName() + ":PrintQueue: Printing a Job during " + (duration / 1000) + " seconds");
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                queueLock.unlock();
            }
        }
    }
}
