package com.sky.t;
import java.io.IOException;

public class Test_1 {
     
    private int i = 10;
    private Object object = new Object();
     
    public static void main(String[] args) throws IOException  {
        Test_1 test = new Test_1();
        MyThread thread1 = test.new MyThread();
        MyThread thread2 = test.new MyThread();
        thread1.start();
        thread2.start();
    } 
     
     
    class MyThread extends Thread{ 
	@Override
        public void run() {
            synchronized (object) {
                i++;
                System.out.println("i:"+i);
                try {
                    System.out.println("线程"+Thread.currentThread().getName()+"进入睡眠状态");
                    
                    Thread.sleep(10000); 
                } catch (InterruptedException e) {
                    // TODO: handle exception
                }
                System.out.println("线程"+Thread.currentThread().getName()+"睡眠结束");
                i++;
                System.out.println("i:"+i);
            }
        }
    }
}