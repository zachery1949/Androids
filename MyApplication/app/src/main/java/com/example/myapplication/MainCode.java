package com.example.myapplication;

/**
 * 测试类
 * @author DH
 *
 */
public class MainCode {
    public static void main(String[] args) {
        //同一个仓库
        BufferArea ba = new BufferArea();

        //三个生产者
        Producer p1 = new Producer(ba);
        Producer p2 = new Producer(ba);
        Producer p3 = new Producer(ba);
        //三个消费者
        Consumer c1 = new Consumer(ba);
        Consumer c2 = new Consumer(ba);
        Consumer c3 = new Consumer(ba);
        //创建线程，并给线程命名
        Thread t1 = new Thread(p1,"生产者1");
        Thread t2 = new Thread(p2,"生产者2");
        Thread t3 = new Thread(p3,"生产者3");
        Thread t4 = new Thread(c1,"消费者1");
        Thread t5 = new Thread(c2,"消费者2");
        Thread t6 = new Thread(c3,"消费者3");
        //使线程进入就绪状态
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
    }
}
