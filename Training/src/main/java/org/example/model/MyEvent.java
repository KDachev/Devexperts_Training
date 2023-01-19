package org.example.model;

import lombok.Getter;

import java.util.Random;

@Getter
public class MyEvent {
    private final double processTime;

    private final int key;

    public MyEvent(int key){
        Random rand = new Random();
        this.processTime = rand.nextDouble(5);
        this.key = key;
    }
}
