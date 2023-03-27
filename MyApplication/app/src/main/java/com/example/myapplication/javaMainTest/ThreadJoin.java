package com.example.myapplication.javaMainTest;

public class ThreadJoin {
    public static void main(String[] args) {
        Long begin = System.currentTimeMillis();

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("子线程1执行");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("子线程2执行");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        thread1.start();
        thread2.start();
        try {
            //主线程开始等待子线程thread1，thread2
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //等待两个线程都执行完（不活动）了，才执行下行打印
        System.out.println("执行完毕");
        System.out.println("结果" + ",耗时:" + (System.currentTimeMillis() - begin) / 1000);
    }

}
