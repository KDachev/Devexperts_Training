package org.example.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UniqueEventsQueue<T> {
    private Queue<T> uniqueEventsQueue;
    private Map<Integer, T> uniqueElementsMap;

    public UniqueEventsQueue(){
        uniqueEventsQueue = new ConcurrentLinkedQueue<>();
        uniqueElementsMap = new HashMap<>();
    }

    public synchronized void add(T element){
        if (!uniqueElementsMap.containsKey(element.hashCode())) {
            uniqueElementsMap.put(element.hashCode(), element);
            uniqueEventsQueue.add(element);
        }
    }

    public T get (){
        return uniqueEventsQueue.poll();
    }

    @Override
    public String toString() {
        return uniqueEventsQueue.toString();
    }
}
