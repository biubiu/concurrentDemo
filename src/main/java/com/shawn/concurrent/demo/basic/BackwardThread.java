package com.shawn.concurrent.demo.basic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BackwardThread {

    public static void main(String[] args) throws InterruptedException {    	
        //runBySync();                
        runByReentrantLock();
    }

    static SharedObject so = new SharedObject();
    static void runBySync(){
    	Thread[] threads = new Thread[10];
    	for (int i = 0; i < threads.length; i++) {
			threads[i] = new ThreadSync(i,so);
			threads[i].start();
		}    	

    }
    static void runByReentrantLock(){
        Thread[] threads = new Thread[10];
        Doc doc = new Doc();
        for( int i=0; i< threads.length; i++){
            threads[i] = new Thread(new DocPrint(i, doc),i+"");
            threads[i].start();
            //threads[i].
        }
    }
}

class ThreadSync extends Thread{
    SharedObject sharedObject;
    int num;
    public ThreadSync(int i,SharedObject sharedObject) {
        this.sharedObject =sharedObject;
        num = i;
    }

    @Override
    public void run() {
        sharedObject.printSelf(num);
    }
}

class SharedObject {
	
	private Integer numberOfThreads = 10;
	//private final Object mutex = new Object(); 
	public void printSelf(int num){
		/*try {
			TimeUnit.SECONDS.sleep(1);
			System.out.println("in sleep");
		} catch (InterruptedException e) {				
			e.printStackTrace();
		}*/
		//System.out.println(this.toString());
		try {
			TimeUnit.MILLISECONDS.sleep(500);
			System.out.println("sleep:" +Thread.currentThread().getName());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		synchronized (this) {
			while((num+1) != numberOfThreads){
				try {
					//System.out.println("before wait: " + Thread.currentThread().getName());
					wait();
					//System.out.println("after wait: " + Thread.currentThread().getName());
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
			System.out.println( num + " "+Thread.currentThread().getName());			
			numberOfThreads--;
			notifyAll();			
		}		
	}	
}


class DocPrint implements Runnable{
    private Doc doc;
    private int id;
    public DocPrint(int id,Doc doc) {
        this.id = id;
        this.doc = doc;
    }
    public void run() {
      doc.printSelf(id);
    }

}
class Doc {
    private Condition number;
    private ReentrantLock lock;
    private int numOfThread = 10;

    public Doc() {
        lock = new ReentrantLock();
        number = lock.newCondition();
    }

    public void printSelf(int id) {
        lock.lock();
        try {         
            while(id+1 != numOfThread) {
                //System.out.printf("%s is waiting...\n",Thread.currentThread().getName());
                number.await();
            }
        	System.out.println( id + " "+Thread.currentThread().getName());            
            numOfThread--;
           number.signalAll();            
        } catch (InterruptedException e) {
            Thread.interrupted();
        }finally{
            lock.unlock();
        }
    }
}
