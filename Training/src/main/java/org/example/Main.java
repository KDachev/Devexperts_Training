package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.UniqueEventsQueue;


public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        UniqueEventsQueue<Integer> ueq = new UniqueEventsQueue<>();

        Thread thread1 = new Thread(() -> {
            System.out.println("Get first element of empty queue: ");
            try {
                System.out.println("Thread 1 pulled a value from the queue: " + ueq.get());
            } catch (InterruptedException e) {
                logger.warn("A thread was interrupted with message: " + e.getMessage());
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("Adding element 1 to queue" );
                ueq.add(1);
                Thread.sleep(200);
                System.out.println("Adding element 2 to queue" );
                ueq.add(2);
                Thread.sleep(200);
                System.out.println("Adding element 3 to queue" );
                ueq.add(3);
            } catch (InterruptedException e){
                logger.warn("A thread was interrupted with message: " + e.getMessage());
            }
        });

        thread1.start();
        thread2.start();
    }
}