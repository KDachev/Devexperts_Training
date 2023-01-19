package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.CustomEventsQueue;
import org.example.model.MyEvent;
import org.example.model.UniqueEventsQueue;
import org.example.service.CSVSorterService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private List<Thread> threadPool = new ArrayList<>();

    public static void main(String[] args) {
        final long startTime = System.nanoTime();
        //testUniqueEventsQueue();
        //testCsvSorterService("count", "area");
        testCustomEventsQueue();

        final long duration = System.nanoTime() - startTime;
        System.out.println("The program ran for " + (double) duration/1000000000 + " seconds");
    }

    public static void testUniqueEventsQueue(){
        UniqueEventsQueue<Integer> ueq = new UniqueEventsQueue<>();

        Thread thread1 = new Thread(() -> {
            System.out.println("Get first element of empty queue: ");
            System.out.println("Thread 1 pulled a value from the queue: " + ueq.get());
            System.out.println("Thread 1 pulled a value from the queue: " + ueq.get());
            System.out.println("Thread 1 pulled a value from the queue: " + ueq.get());
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
                System.out.println("Adding element 1 to queue" );
                ueq.add(1);
            } catch (InterruptedException e) {
                logger.warn("A thread was interrupted with message: " + e.getMessage());
            }
        });

        thread1.start();
        thread2.start();
    }

    public static void testCsvSorterService(String... compareOnFields){
        File inputFile = new File("C:\\Users\\kdachev\\IdeaProjects\\Devexperts_Training\\Training\\DATA8277.csv");
        File outputFile = new File("C:\\Users\\kdachev\\IdeaProjects\\Devexperts_Training\\Training\\result.csv");

        CSVSorterService csvSorterService = new CSVSorterService();
        csvSorterService.sort(inputFile, outputFile, compareOnFields);
    }

    public static void testCustomEventsQueue(){
        CustomEventsQueue customEventsQueue = new CustomEventsQueue();
        MyEvent event1 = new MyEvent(1);
        MyEvent event2 = new MyEvent(1);
        MyEvent event3 = new MyEvent(2);
        MyEvent event4 = new MyEvent(2);
        MyEvent event5 = new MyEvent(2);
        MyEvent event6 = new MyEvent(3);

        customEventsQueue.addEvent(event1);
        customEventsQueue.addEvent(event2);
        customEventsQueue.addEvent(event3);
        customEventsQueue.addEvent(event4);
        customEventsQueue.addEvent(event5);
        customEventsQueue.addEvent(event6);

        Thread thread1 = new Thread(customEventsQueue::processEvent);
        Thread thread2 = new Thread(customEventsQueue::processEvent);
        Thread thread3 = new Thread(customEventsQueue::processEvent);
        Thread thread4 = new Thread(customEventsQueue::processEvent);
        Thread thread5 = new Thread(customEventsQueue::processEvent);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

    }
}