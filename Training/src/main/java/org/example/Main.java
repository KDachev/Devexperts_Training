package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.UniqueEventsQueue;
import org.example.service.CSVSorterService;

import java.io.File;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        final long startTime = System.nanoTime();
        //testUniqueEventsQueue();
        testCsvSorterService("count", "area");
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
}