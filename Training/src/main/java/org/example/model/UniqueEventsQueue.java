package org.example.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UniqueEventsQueue<T> {
    private static final Logger logger = LogManager.getLogger(UniqueEventsQueue.class);
    private final Queue<T> uniqueEventsQueue;
    private final Set<T> uniqueEventsSet;

    public UniqueEventsQueue(){
        uniqueEventsQueue = new ConcurrentLinkedQueue<>();
        uniqueEventsSet = new HashSet<>();
    }

    public void add(T event){
        synchronized (uniqueEventsQueue) {
            if (uniqueEventsSet.add(event)) {
                uniqueEventsQueue.add(event);
                uniqueEventsQueue.notify();
            }
        }
    }

    public T get () {
        synchronized(uniqueEventsQueue) {
            while (uniqueEventsQueue.isEmpty()) {
                try {
                    System.out.println("Thread is blocked until queue isn't empty");
                    uniqueEventsQueue.wait();
                } catch (InterruptedException e) {
                    logger.warn("A thread was interrupted with message: " + e.getMessage());
                }
            }
            T answer = uniqueEventsQueue.poll();
            uniqueEventsSet.remove(answer);
            return answer;
        }
    }

    @Override
    public String toString() {
        return uniqueEventsQueue.toString();
    }
}
