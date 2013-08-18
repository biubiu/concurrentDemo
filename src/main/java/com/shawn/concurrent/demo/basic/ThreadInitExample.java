package com.shawn.concurrent.demo.basic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadInitExample {
	public static void main(String[] args) {
		//LiftOffRun();
		executorRun();
	}
	
	static void LiftOffRun(){
		Thread[] threads = new Thread[10];
		for (int i = 0; i < threads.length; i++) {
			Thread thread = new Thread(new LiftOff());
			thread.start();
		}
		/*
		LiftOff off = new LiftOff(10);
		Thread thread  = new Thread(off);
		off.run();*/
		//thread.start();		
	}
	static void executorRun(){
		//ExecutorService executorService = Executors.newCachedThreadPool();//create as many threads as needed
		//ExecutorService executorService = Executors.newSingleThreadExecutor();//each tasks will be queued and run to completion b4 the next is begun
		ExecutorService executorService = Executors.newFixedThreadPool(5);//threads are reused when possible
		for (int i = 0; i < 5; i++) {
			executorService.execute(new LiftOff());			
		}
		executorService.shutdown();
	}
}

class LiftOff implements Runnable{
	int countDown = 10;
	private static int taskCount =0;
	private final int id = taskCount++;
	
	public LiftOff() {			
	}
	public LiftOff(int countDown){
		this.countDown = countDown;
	}
	
	public String status(){
		return "#" + id + "("+(countDown >0 ?countDown:"liftOff!")+")";
	}
	
	public void run() {
		while (countDown-- > 0) {
			System.out.println(status());
			Thread.yield();//moves CPU from one thread to another
		}
	}
	
}
