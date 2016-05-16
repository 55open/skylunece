package com.sky.t;

import java.io.IOException;

public class Test_3 {
     
    public static void main(String[] args) throws IOException  {
        Test_3 test = new Test_3();
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
            try {
                System.out.println("进入睡眠状态");
                Thread.sleep(10000);
                System.out.println("睡眠完毕");
            } catch (InterruptedException e) {
                System.out.println("得到中断异常");
            }
            System.out.println("run方法执行完毕");
        }
    }
}