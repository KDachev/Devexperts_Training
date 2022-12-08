package org.example.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UniqueEventsQueue<T> {
    private final Queue<T> uniqueEventsQueue;
    private final Map<Integer, T> uniqueElementsMap;
    private final Object getLock = new Object();
    private final Object addLock = new Object();

    public UniqueEventsQueue(){
        uniqueEventsQueue = new ConcurrentLinkedQueue<>();
        uniqueElementsMap = new HashMap<>();
    }

    public void add(T element){
        synchronized (uniqueEventsQueue) {
            if (!uniqueElementsMap.containsKey(element.hashCode())) {
                uniqueElementsMap.put(element.hashCode(), element);
                uniqueEventsQueue.add(element);
                uniqueEventsQueue.notify();
            }
        }
    }

    public T get () throws InterruptedException {
        synchronized(uniqueEventsQueue) {
            if (uniqueEventsQueue.isEmpty()) {
                System.out.println("Thread goes to sleep");
                uniqueEventsQueue.wait();
            }
            return uniqueEventsQueue.poll();
        }
}

    @Override
    public String toString() {
        return uniqueEventsQueue.toString();
    }
}
