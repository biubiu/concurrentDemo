package com.shawn.concurrent.demo.util;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.google.common.collect.Lists;

public class ProducerConsumerDemo {
	public static void main(String[] args) {
		//Storage storage = new StorageWithSync(10);		
		 //Storage storage = new StorageWithCondition(10);
		//Storage storage = new StorageReadWriteLock(10);
		Storage storage = new StorageWithSemaphore(10);
		
		ExecutorService executor = Executors.newCachedThreadPool();		
		try {
			executor.invokeAll(Lists.newArrayList(new Producer(storage),new Consumer(storage)));
			//executor.invokeAll(Lists.newArrayList(new Producer(storage),new Viewer(storage), new Viewer(storage)));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
	}

}




class Consumer implements Callable<String> {
	private Storage storage;

	public Consumer(Storage storage) {
		this.storage = storage;
	}

	
	public String call() throws Exception {
		for (int i = 0; i < 10; i++) {
			try {
				TimeUnit.NANOSECONDS.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			storage.get();
		}
		return null;
	}

}

class Producer implements Callable<String> {
	private Storage storage;

	public Producer(Storage storage) {
		this.storage = storage;
	}

	public String call() throws Exception {
		for (int i = 0; i < 10; i++) {			
			storage.set();
		}
		return null;
	}
}

//for read write lock example
class Viewer implements Callable<String> {
	private Storage storage;

	public Viewer(Storage storage) {
		this.storage = storage;
	}

	public String call() throws Exception{
		for (int i = 0; i < 10; i++) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			storage.read();
		}
		return null;
	}
}

class StorageWithSync extends Storage {
	int maxSize;
	LinkedList<Date> storage;

	public StorageWithSync(int maxSize) {
		this.maxSize = maxSize;
		storage = new LinkedList<Date>();
	}

	public synchronized void set() {
		while (storage.size() == maxSize) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		storage.offer(new Date());
		System.out.printf("Set: %d \n", storage.size());
		notifyAll();
	}

	public synchronized void get() {
		while (storage.size() == 0) {
			try {				
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		System.out.printf("Get: %d: %s \n", storage.size(),
				((LinkedList<?>) storage).poll());
		notifyAll();
	}
}

class StorageWithCondition extends Storage {
	int maxSize;
	LinkedList<Date> storage;
	private final ReentrantLock reentrantLock = new ReentrantLock(true);
	private Condition notFull = reentrantLock.newCondition();
	private Condition notEmpty = reentrantLock.newCondition();

	public StorageWithCondition(int maxSize) {
		this.maxSize = maxSize;
		storage = new LinkedList<Date>();
	}

	public void set() {
		reentrantLock.lock();
		try {
			while (storage.size() == maxSize) {
				notFull.await();
			}
			storage.offer(new Date());
			System.out.printf("Set: %d \n", storage.size());
			notEmpty.signalAll();
		}  catch (InterruptedException e) {
			System.out.printf("%s Interrupted",Thread.currentThread().getName());
		}catch(IllegalMonitorStateException e){
			System.out.printf("%s is locked by illegal monitor",Thread.currentThread().getName());
		} finally {
			reentrantLock.unlock();
		}
	}

	public void get() {
		reentrantLock.lock();
		try {
			while (storage.size() == 0) {
				notEmpty.await();
			}
			System.out.printf("Get: %d: %s \n", storage.size(), storage.poll());
			notFull.signalAll();
		} catch (InterruptedException e) {
			System.out.printf("%s Interrupted",Thread.currentThread().getName());
		}catch(IllegalMonitorStateException e){
			System.out.printf("%s is locked by illegal monitor",Thread.currentThread().getName());
		} finally {
			reentrantLock.unlock();
		}
	}

}

class StorageWithSemaphore extends Storage {
	int maxSize;
	LinkedList<Date> storage;
	private final Semaphore notFull;
	private final Semaphore notEmpty = new Semaphore(0,true);
	private final Semaphore access = new Semaphore(1,true);

	public StorageWithSemaphore(int maxSize) {
		this.maxSize = maxSize;
		storage = new LinkedList<Date>();
		notFull = new Semaphore(maxSize,true);
	}

	public void set() {
		try {
			notFull.acquire();
			access.acquire();
			storage.offer(new Date());
			System.out.printf("Set: %d \n", storage.size());
		} catch (InterruptedException e) {
			System.out.println(e.toString());
		} finally {
			access.release();
			notEmpty.release();
		}
	}

	public void get() {

		try {
			notEmpty.acquire();
			access.acquire();
			System.out.printf("Get: %d: %s \n", storage.size(), storage.poll());
		} catch (InterruptedException e) {
			System.out.println(e.toString());
		} finally {
			access.release();
			notFull.release();
		}
	}
}


class StorageReadWriteLock extends Storage {
	int maxSize;
	private LinkedList<Date> storage;
	private ReentrantReadWriteLock lock;
	private ReadLock readlock;
	private WriteLock writeLock;
	private Condition notFull;
	private Condition notEmpty;

	public StorageReadWriteLock(int maxSize) {
		this.maxSize = maxSize;
		storage = Lists.newLinkedList();
		lock = new ReentrantReadWriteLock();
		readlock = lock.readLock();
		writeLock = lock.writeLock();
		notFull = writeLock.newCondition();
		notEmpty = writeLock.newCondition();
	}

	public void set() {
		writeLock.lock();
		try {
			while (storage.size() == maxSize) {
				notFull.await();
			}
			storage.offer(new Date());
			System.out.printf("Set: %d \n", storage.size());
			notEmpty.signalAll();
		} catch (InterruptedException e) {
			System.out.printf("%s Interrupted",Thread.currentThread().getName());
		}catch(IllegalMonitorStateException e){
			System.out.printf("%s is locked by illegal monitor",Thread.currentThread().getName());
		} finally {
			writeLock.unlock();
		}
	}

	public void get() {
		writeLock.lock();
		try {
			while (storage.size() == 0) {
				notEmpty.await();
			}
			System.out.printf("Get: %d: %s \n", storage.size(), storage.poll());
			notFull.signalAll();
		} catch (InterruptedException e) {
			System.out.printf("%s Interrupted",Thread.currentThread().getName());
		}catch(IllegalMonitorStateException e){
			System.out.printf("%s is locked by illegal monitor",Thread.currentThread().getName());
		} finally {			
			writeLock.unlock();
		}
	}

	@Override
	public void read() {
		readlock.lock();
		try {
			if (storage.size() != 0) {
				System.out.printf("%s read: %d : %s \n", Thread.currentThread()
						.getId(), storage.size(),
						storage.get(storage.size() - 1));
			} else {				
				Thread.currentThread().interrupt();
			}
		} finally {
			
			readlock.unlock();
		}
	}
}

abstract class Storage {

	public abstract void set();

	public abstract void get();

	public void read() throws Exception {
		throw new UnsupportedOperationException("not implemented");
	}

}