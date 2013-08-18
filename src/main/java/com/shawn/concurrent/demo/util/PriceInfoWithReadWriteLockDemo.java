package com.shawn.concurrent.demo.util;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import com.google.common.collect.Lists;

public class PriceInfoWithReadWriteLockDemo {
    public static void main(String[] args) {
    	PricesInfo pricesInfo = new PricesInfo();    	
    	List<Thread> list = Lists.newArrayList();
    	    	
        for (int i=0; i<5; i++){        	
        	list.add(new Thread(new Reader(pricesInfo),"reader"+i));
        }
        list.add(new Thread(new Writer(pricesInfo),"writer "));
        
        for (Thread thread:list) {
			thread.start();
		}
        
    }
}


class Reader implements Runnable{
	private PricesInfo pricesInfo;
	public Reader(PricesInfo pricesInfo) {
		this.pricesInfo = pricesInfo;
	}
	public void run() {
		for (int i=0; i<10; i++){
		     System.out.printf("%s: Price 1: %f\n", Thread.currentThread().getName(),pricesInfo.getPrice1());
		     System.out.printf("%s: Price 2: %f\n", Thread.currentThread().getName(),pricesInfo.getPrice2());
		  }
	}
	
}
class Writer implements Runnable{
	private PricesInfo pricesInfo;
	public Writer(PricesInfo pricesInfo) {
		this.pricesInfo = pricesInfo;
	}
	
	public void run() {	
		double price1 = Math.random()*10;
		double price2 =  Math.random()*8;
		 try {
			 TimeUnit.MILLISECONDS.sleep(20);
		 } catch (InterruptedException e) {
			 	e.printStackTrace();
		 }
		 pricesInfo.setPrices(price1, price2);		
	}
}

class PricesInfo{
	private double price1;
	private double price2;
	
	final private ReadLock readLock;
	final private WriteLock writeLock;
	public PricesInfo(){
		price1=1.0;
		price2=2.0;
		ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		readLock = readWriteLock.readLock();
		writeLock = readWriteLock.writeLock();
	}

	public double getPrice1() {
		readLock.lock();
		double value = price1;
		readLock.unlock();
		return value;
	}

	public double getPrice2() {
		readLock.lock();
		double value = price2;
		readLock.unlock();
		return value;
	}
	
	public void setPrices(double price1,double price2) {
	 writeLock.lock();
	 System.out.printf("%s: Attempt to modify the prices with price1 %f, price2 %f.\n",Thread.currentThread().getName(),price1,price2);
	 this.price1 = price1;
	 this.price2 = price2;
	 System.out.printf("%s: Prices have been modified.\n",Thread.currentThread().getName());
	 writeLock.unlock();	 
	}				
}