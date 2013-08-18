package com.shawn.concurrent.demo.atomic;

public class AtomicInteger {

    public static void main(String[] args) {
       final IntClass intClass = new IntClass();
        Thread[] threads = new Thread[1000];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    intClass.a();
                }
            }, i+"");
            threads[i].start();
        }
    }
}

class IntClass{
    int i = 0;

    public void a(){
        ++i;
        System.out.println(i);
    }
}
