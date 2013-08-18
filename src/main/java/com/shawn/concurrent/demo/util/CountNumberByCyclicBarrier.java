package com.shawn.concurrent.demo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.ListModel;

import com.google.common.collect.Lists;

public class CountNumberByCyclicBarrier {

	 	private long sum;  
	 //	private static CyclicBarrier barrier;  
	    private List<Integer> list;  
	    private int threadCounts;  
	    	    
	    public static void main(String[] args) {  
	        List<Integer> list = new ArrayList<Integer>();  
	        int threadCounts = 10;  
	  //      barrier=new CyclicBarrier(threadCounts+1);
	        for (int i = 1; i <= 1000000; i++) {  
	            list.add(i);  
	        }  
	        CountNumberByCyclicBarrier countListIntegerSum=new CountNumberByCyclicBarrier(list,threadCounts);  
	        long sum=countListIntegerSum.divideAndConquerSum();  
	        System.out.println("sum:"+sum);  
	    } 
	    
	    public CountNumberByCyclicBarrier(List<Integer> list,int threadCounts) {  
	        this.list=list;  
	        this.threadCounts=threadCounts;  
	    }  
	
	    public long divideAndConquerSum(){  
	        ExecutorService exec=Executors.newFixedThreadPool(threadCounts);  
	        int len=list.size()/threadCounts;//split list  
	        if(len==0){  
	            threadCounts=list.size();  
	            len=list.size()/threadCounts;  
	        }  
	        
	        List<Future<Long>> subSums = Lists.newArrayList();
	        for(int i=0;i<threadCounts;i++){  
	            if(i==threadCounts-1){  
	                subSums.add(exec.submit(new SubIntegerSumTask(list.subList(i*len,list.size()))));	            		            
	            }else{  	            	
	                subSums.add(exec.submit(new SubIntegerSumTask(list.subList(i*len, len*(i+1)>list.size()?list.size():len*(i+1)))));	            	
	            }  
	        }  
	        
	       try {
	    	//   barrier.await();//threads waiting in barrier	    	   			              	           
	    	   for (Future<Long> e:subSums) {
				sum+=e.get();
			}
	        } catch (InterruptedException e) {  
	            System.out.println(Thread.currentThread().getName()+" Interrupted");  
	        }/* catch (BrokenBarrierException e) {  
	            System.out.println(Thread.currentThread().getName()+" BrokenBarrier");  
	        }*/ catch (ExecutionException e1) {
				System.out.println(Thread.currentThread().getName() +"executuon exception");
			}  
	        exec.shutdown();  
	        return sum;  
	    } 


	     class SubIntegerSumTask implements Callable<Long>{  
	        private List<Integer> subList;  
	        
	        public SubIntegerSumTask(List<Integer> subList) {  
	            this.subList=subList;  
	        }  	      
			
			public Long call() throws Exception {
				 long subSum=0L;
				 if(!"pool-1-thread-10".equals(Thread.currentThread().getName())){
		            for (Integer i : subList) {  
		                subSum += i;  
		            }		 		            
		            System.out.println("allocating to:"+Thread.currentThread().getName()+" total \tSubSum "+subSum);
				 }else{
					// Thread.currentThread().interrupt();
					 throw new Exception();
				 }
		        /*   try {  
		                barrier.await();  
		            } catch (InterruptedException e) {  
		                System.out.println(Thread.currentThread().getName()+":Interrupted");  
		            } catch (BrokenBarrierException e) {  
		                System.out.println(Thread.currentThread().getName()+":BrokenBarrier");  
		            }*/
		              
				return subSum;
			}			
	    }  
}
