package com.sky.t;

import java.io.IOException;

public class Test_4 {
     
    public static void main(String[] args) throws IOException  {
        Test_4 test = new Test_4();
        MyThread thread = test.new MyThread();
        thread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
             
        }
        thread.interrupt();
    } 
     
    class MyThread extends Thread{
        @Override
        public void run() {
            int i = 0;
            while(i<Integer.MAX_VALUE){
                System.out.println(i+" while循环");
                i++;
            }
        }
    }
}