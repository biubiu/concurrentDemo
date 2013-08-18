package com.shawn.concurrent.demo.basic;

public class ReentrantLockTest {

    static ShareObject so;

    public static void main(String[] args) {
        //final Lock lock = new SimpleLock();

        final Lock lock= new ReentrantLock();
        Thread[] threads = new Thread[5];
        so = new ShareObject(lock);
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        System.out.printf("%s require the lock A \n", Thread.currentThread().getName());
                        lock.lock();
                        System.out.printf("%s got the lock A \n", Thread.currentThread().getName());
                        so.a();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        System.out.printf("%s release the lock A \n", Thread.currentThread().getName());
                        lock.unlock();
                    }
                }
            }, i + "");
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
    }
}

class ShareObject {
    private Lock lock;

    public ShareObject(Lock lock) {
        this.lock = lock;
    }

    public void a() {
        try {
            System.out.printf("%s require the lock B \n", Thread.currentThread().getName());
            lock.lock();
            System.out.printf("%s got the lock b \n", Thread.currentThread().getName());
            b();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.printf("%s release the lock B \n", Thread.currentThread().getName());
            lock.unlock();
        }

    }

    public void b() {

    }
}

class ReentrantLock implements Lock {
    Thread lockedBy = null;
    boolean isLocked = false;
    int count = 0;

    public synchronized void lock() throws InterruptedException {
        while (count != 0 && lockedBy != Thread.currentThread()) {
            wait();
        }
        isLocked = true;
        lockedBy = Thread.currentThread();
        count++;
    }

    public synchronized  void unlock() {
        if (lockedBy == Thread.currentThread()) {
            count--;
            if (count == 0) {
                isLocked = false;
                notify();
            }
        }
    }

}

class SimpleLock implements Lock {
    Thread currentThread = null;
    boolean isLock = false;

    public synchronized  void lock() throws InterruptedException {
        while (isLock) {
            wait();
        }
        currentThread = Thread.currentThread();
        isLock = true;
    }

    public synchronized  void unlock() {
        if (currentThread == Thread.currentThread()) {
            isLock = false;
            notifyAll();
        }
    }

}

interface Lock {
    public void lock() throws InterruptedException;

    public void unlock();
}