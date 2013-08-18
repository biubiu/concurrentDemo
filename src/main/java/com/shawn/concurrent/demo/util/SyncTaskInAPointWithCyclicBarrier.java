package com.shawn.concurrent.demo.util;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;

public class SyncTaskInAPointWithCyclicBarrier {
    public static void main(String[] args) {
        final int ROWS = 10000;
        final int NUMBERS = 1000;
        final int SEARCH = 5;
        final int PARTICIPANT =5;
        final int LINES_PARTICIPANT=2000;
        
        MatrixMock mock = new MatrixMock(ROWS, NUMBERS,SEARCH);
        Results results = new Results(ROWS);

        Grouper grouper = new Grouper(results);
        CyclicBarrier barrier = new CyclicBarrier(PARTICIPANT,grouper);
        Searcher searchers[] = new Searcher[PARTICIPANT];

        for (int i = 0; i < searchers.length; i++) {
            searchers[i] = new Searcher(i*LINES_PARTICIPANT,(i*LINES_PARTICIPANT)+LINES_PARTICIPANT,
                                        mock, results, 9, barrier);
            Thread thread = new Thread(searchers[i]);
            thread.start();
        }
        System.out.printf("Main thread has finished.\n");
    }
}


class Grouper implements Runnable{
    private Results results;
    public Grouper(Results results) {
        this.results = results;
    }
    public void run() {
        int finalResult = 0;
        System.out.printf("Grouper: Processing results ...\n");
        int data[] = results.getDta();
        for(int number:data){
            finalResult += number;
        }
        System.out.printf("Grouper: Total result: %d \n",finalResult);
    }

}

class Searcher implements Runnable{
    private int firstRow;
    private int lasRow;
    private MatrixMock mock;
    private Results results;
    private int number;
    private final CyclicBarrier barrier;


    public Searcher(int firstRow, int lasRow, MatrixMock mock, Results results, int number, CyclicBarrier barrier) {
        this.firstRow = firstRow;
        this.lasRow = lasRow;
        this.mock = mock;
        this.results = results;
        this.number = number;
        this.barrier = barrier;
    }


    public void run() {
        int counter;
        System.out.printf("%s: Processing lines from %d to %d .\n",Thread.currentThread().getName(),firstRow,lasRow);
        for (int i = firstRow; i <lasRow ; i++) {
            int row[] = mock.getRow(i);
            counter = 0;
            for (int j = 0; j < row.length; j++) {
                if(row[j] == number){
                    counter++;
                }
            }
            results.setData(i, counter);
        }
        System.out.printf("%s:  lines processed .\n",Thread.currentThread().getName());
        try{
            barrier.await();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

//generate a random matrix of numbers between one and 10 where the threads are going to look for a number.
class MatrixMock {
    private int data[][];

    //Each time you generate a number, compare it with the number you are going to look for. 
    //If they are equal, increment the counter.
    public MatrixMock(int size, int length, int number) {
        int counter = 0;
        data = new int[size][length];        
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < length; j++) {
                data[i][j] = random.nextInt(10);
                if(data[i][j] == number)
                    counter++;
            }
        }
        System.out.printf("Mock: There are %d ocurrences of number in generated data . \n",counter,number);
    }

    public int[] getRow(int row){
        if((row>=0) && (row<data.length)){
            return data[row];
        }
        return null;
    }
}

//store the number of occurrence of the searched number in each row of matrix
class Results{
    private int data[];

    public Results(int size){
        data = new int[size];
    }

    public void setData(int position, int value){
        data[position] = value;
    }

    public int[] getDta(){
        return data;
    }
}
