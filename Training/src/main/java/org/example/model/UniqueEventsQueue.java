package org.example.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UniqueEventsQueue<T> {
    private static final Logger logger = LogManager.getLogger(UniqueEventsQueue.class);
    private final Queue<T> uniqueEventsQueue;
    private final Map<Integer, T> uniqueEventsMap;

    public UniqueEventsQueue(){
        uniqueEventsQueue = new ConcurrentLinkedQueue<>();
        uniqueEventsMap = new HashMap<>();
    }

    public void add(T element){
        synchronized (uniqueEventsQueue) {
            if (!uniqueEventsMap.containsKey(element.hashCode())) {
                uniqueEventsMap.put(element.hashCode(), element);
                uniqueEventsQueue.add(element);
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
            uniqueEventsMap.remove(answer.hashCode());
            return answer;
        }
    }

    @Override
    public String toString() {
        return uniqueEventsQueue.toString();
    }
}
