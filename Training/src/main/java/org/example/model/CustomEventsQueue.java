package org.example.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class CustomEventsQueue{
    private static final Logger logger = LogManager.getLogger(CustomEventsQueue.class);

    private final BlockingQueue<MyEvent> queue;

    private final Map<Integer, ReentrantLock> keys;

    public CustomEventsQueue(){
        this.queue = new LinkedBlockingQueue<>();
        this.keys = new ConcurrentHashMap<>();
    }

    public void processEvent(){
        MyEvent myEvent = null;
        try {
            myEvent = queue.take();
            // tried ReentrantLock instead of locking an Object
            ReentrantLock lock = keys.computeIfAbsent(myEvent.getKey(), k -> new ReentrantLock());
            lock.lock();
            try {
                //For testing if threads wait for each-other
                long sleepTime = (long) (myEvent.getProcessTime() * 1000);
                Thread.sleep(sleepTime);
                System.out.println("Event with key " + myEvent.getKey() + " processed for " + sleepTime);
            } finally {
                keys.remove(myEvent.getKey());
                lock.unlock();
            }
        } catch (InterruptedException e) {
            logger.warn("A thread was interrupted with message: " + e.getMessage());
        }
    }

    public void addEvent(MyEvent myEvent){
        queue.add(myEvent);
    }
}
