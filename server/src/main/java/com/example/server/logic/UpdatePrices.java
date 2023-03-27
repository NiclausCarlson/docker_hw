package com.example.server.logic;

public class UpdatePrices {
    private static long startTime = System.currentTimeMillis();
    private static final long KUpdateInterval = 2*60*1000; // two minutes

    public static boolean check(){
        long currentTime = System.currentTimeMillis();
        if(currentTime - startTime >= KUpdateInterval){
            startTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

}
