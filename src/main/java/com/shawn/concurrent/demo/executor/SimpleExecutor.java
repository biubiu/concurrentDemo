package com.shawn.concurrent.demo.executor;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleExecutor {
    public static void main(String[] args) {
        ExecutorServer server = new ExecutorServer();
        for (int i = 0; i < 100; i++) {
            Task task = new Task("Task " + i);
            server.executeTask(task);
        }
       server.endServer();
    }
}

class Task implements Runnable{
    private Date initDate;
    private String name;

    public Task(String name){
        initDate = new Date();
        this.name = name;
    }

    public void run() {
        System.out.printf("%s: Task %s: Created on: %s \n",Thread.currentThread().getName(),name,initDate);
        System.out.printf("%s: Task %s: Started on: %s \n",Thread.currentThread().getName(),name,new Date());
        try {
            Long duration = (long)(Math.random()*10);
            System.out.printf("%s: Task %s: Doing a task during %d seconds \n",Thread.currentThread().getName(),name,duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Task %s:Finished on: %s \n",Thread.currentThread().getName(),name,new Date());
    }
}

class ExecutorServer {
    private ThreadPoolExecutor executor;
    public ExecutorServer() {
        //executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
        executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(50);
    }

    public void executeTask(Task task){
        System.out.printf("Server: A new task has arrived \n");
        executor.execute(task);
        System.out.printf("Server: largest number pool size: %d \n",executor.getLargestPoolSize());
        System.out.printf("Server: pool Size: %d \n",executor.getPoolSize());
        System.out.printf("Server: Active count; %d\n",executor.getActiveCount());
        System.out.printf("Server: Completed Tasks: %d\n",executor.getCompletedTaskCount());
    }

    public void endServer(){
        executor.shutdown();
    }
}
