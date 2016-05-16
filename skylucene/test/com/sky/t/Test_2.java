package com.sky.t;

import java.io.IOException;

public class Test_2 {
     
    public static void main(String[] args) throws IOException  {
        System.out.println("进入线程"+Thread.currentThread().getName());
        Test_2 test = new Test_2();
        MyThread thread1 = test.new MyThread();
        thread1.start();
        try { 
            System.out.println("线程"+Thread.currentThread().getName()+ " " +Thread.currentThread().getState());
            thread1.join(100l);
            System.out.println("线程"+Thread.currentThread().getName()+"继续执行");
        } catch (InterruptedException e) { 
            e.printStackTrace();
        }
    } 
     
    class MyThread extends Thread{
        @Override
        public void run() {
            System.out.println("进入线程"+Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO: handle exception
            }
            System.out.println("线程"+Thread.currentThread().getName()+"执行完毕");
        }
    }
}