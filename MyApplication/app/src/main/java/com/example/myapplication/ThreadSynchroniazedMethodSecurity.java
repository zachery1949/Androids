package com.example.myapplication;

public class ThreadSynchroniazedMethodSecurity {


    static int tickets = 10;

    class SellTickets implements Runnable{

        @Override
        public void run() {
            //同步方法
            while (tickets > 0) {

                synMethod();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (tickets<=0) {

                    System.out.println(Thread.currentThread().getName()+"--->售票结束");
                }

            }


        }

         void synMethod() {

            synchronized (this) {
                if (tickets <=0) {

                    return;
                }

                System.out.println(Thread.currentThread().getName()+"---->售出第 "+tickets+" 票 ");
                tickets-- ;
            }

        }

    }
    public static void main(String[] args) {


        SellTickets sell = new ThreadSynchroniazedMethodSecurity().new SellTickets();

        Thread thread1 = new Thread(sell, "1号窗口");
        Thread thread2 = new Thread(sell, "2号窗口");
        Thread thread3 = new Thread(sell, "3号窗口");
        Thread thread4 = new Thread(sell, "4号窗口");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

    }

}
