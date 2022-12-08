package org.example.model;

import java.util.ArrayDeque;
import java.util.Queue;

public class UniqueEventsQueue<T> {
    private Queue<T> uniqueEventsQueue;

    public UniqueEventsQueue(){
        uniqueEventsQueue = new ArrayDeque<>();
    }

    public void add(T element){
        if(!uniqueEventsQueue.contains(element)){
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
