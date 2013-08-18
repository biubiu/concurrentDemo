package com.shawn.concurrent.demo.basic;

import java.util.concurrent.TimeUnit;

public class InterruptDemo {
	public static void main(String[] args) {
		Thread task = new Thread(new PrimeGenerator());		
		task.start();
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		task.interrupt();	
		
	}
}

class PrimeGenerator implements Runnable{

	
	public void run() {
		long number = 1L;
		while(true){
			if(isPrime(number)){
				System.out.printf("Number %d is Prime \n ",number);
			}
			if(Thread.currentThread().isInterrupted()){
				System.out.println("The prime generator has been interrupted");
				return;
			}
			number++;
		}		
	}
	
	private boolean isPrime(long number){
			if(number <= 2){
				return true;
			}
			for(long i = 2;i<number ; i++){
				if((number %i ) ==0){
					return false;
				}
			}
			return true;
	}
	
}
