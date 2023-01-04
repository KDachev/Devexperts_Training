package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.UniqueEventsQueue;
import org.example.service.CSVSorterService;

import java.io.File;
import java.io.IOException;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
//        UniqueEventsQueue<Integer> ueq = new UniqueEventsQueue<>();
//
//        Thread thread1 = new Thread(() -> {
//            System.out.println("Get first element of empty queue: ");
//            System.out.println("Thread 1 pulled a value from the queue: " + ueq.get());
//            System.out.println("Thread 1 pulled a value from the queue: " + ueq.get());
//            System.out.println("Thread 1 pulled a value from the queue: " + ueq.get());
//        });
//
//        Thread thread2 = new Thread(() -> {
//            try {
//                Thread.sleep(5000);
//                System.out.println("Adding element 1 to queue" );
//                ueq.add(1);
//                Thread.sleep(200);
//                System.out.println("Adding element 2 to queue" );
//                ueq.add(2);
//                Thread.sleep(200);
//                System.out.println("Adding element 1 to queue" );
//                ueq.add(1);
//            } catch (InterruptedException e) {
//                logger.warn("A thread was interrupted with message: " + e.getMessage());
//            }
//        });
//
//        thread1.start();
//        thread2.start();

        File inputFile = new File("C:\\Users\\kdachev\\IdeaProjects\\Devexperts_Training\\Training\\DATA8277.csv");
        File outputFile = new File("C:\\Users\\kdachev\\IdeaProjects\\Devexperts_Training\\Training\\result.csv");

        CSVSorterService csvSorterService = new CSVSorterService();
//        csvSorterService.sort(inputFile, outputFile, "count");
        csvSorterService.sort(inputFile, outputFile);
    }
}