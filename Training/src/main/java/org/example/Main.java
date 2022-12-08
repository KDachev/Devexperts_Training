package org.example;

import org.example.model.UniqueEventsQueue;

public class Main {
    public static void main(String[] args) {
        UniqueEventsQueue<Integer> ueq = new UniqueEventsQueue<>();

        System.out.println("Get first element of empty queue: " + ueq.get());
        ueq.add(1);
        ueq.add(1);
        ueq.add(1);
        ueq.add(1);
        ueq.add(2);
        ueq.add(3);
        System.out.printf("Adding elements 1, 2, 3: \n%s\n", ueq);
        System.out.println("Get first element after adding elements: " + ueq.get());
        System.out.println(ueq);

    }
}