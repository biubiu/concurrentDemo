package com.shawn.concurrent.demo.util;


import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;



public class ExecutorServer {
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Server server = new Server();
        Set<String> sets = Sets.newCopyOnWriteArraySet();
        List<Future> futures = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            Task task = new Task("Task " + i, sets);
            futures.add(server.executeTask(task));
            //server.executeTask(task);
        }
        for (Future<?> future:futures) {
            future.get();
        }
            server.endServer();
            Iterator<String> iterator = sets.iterator();
            while(iterator.hasNext()){
                System.out.println("set ..." + iterator.next());
            }



    }
}



class Server {
    private ThreadPoolExecutor executor;

    Server(){
        executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
    }

    public ThreadPoolExecutor getExecutorInstance(){
        return executor;
    }

    public Future executeTask(Task task){
        System.out.println("Server: new task has arrived \n");
        Future future = (Future) executor.submit(task);
        System.out.printf("Server:%s Pool Size \n",executor.getPoolSize());
        System.out.printf("Server:%s Active thread \n",executor.getActiveCount());
        System.out.printf("Server:%s Completed Tasks \n",executor.getCompletedTaskCount());
        return future;
    }


    public void endServer(){
        executor.shutdown();
    }

}
class Task implements Runnable{

    private Date iniDate;
    private String name;
    private Set<String> set;
    public Task(String name,Set<String> sets) {
        iniDate = new Date();
        this.name = name;
        this.set = sets;
    }
    public void runFun(){
        System.out.printf("%s Task %s \n",Thread.currentThread().getName(),iniDate);
    }
    public void run() {
        runFun();
        try {
            long duration = (long)(Math.random()*10);
            TimeUnit.SECONDS.sleep(duration);
            } catch (Exception e) {
            e.printStackTrace();
        }
        set.add(name);
        System.out.printf("%s : Task %s:finish on : %s \n", Thread.currentThread().getName(),name,new Date());
    }

}