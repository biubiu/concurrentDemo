package com.shawn.concurrent.demo.basic;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;

public class ThreadWithReturnValue {
	public static void main(String[] args) {
		taskWithResult();
	}

	static void taskWithResult() {
		ExecutorService executor = Executors.newCachedThreadPool();
		List<Future<String>> results = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			results.add(executor.submit(new TaskWithResult(i))); //submit produce the future 
		}
		for (Future<String> e : results) {
			try {
				System.out.println(e.get());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}finally{				
				executor.shutdown();
			}
		}
	}
}

class TaskWithResult implements Callable<String> {
	private int id;

	public TaskWithResult(int id) {
		this.id = id;
	}

	
	public String call() {
		return "result of Task with result" + id;
	}

}