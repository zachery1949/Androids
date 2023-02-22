package com.example.myapplication;

/**
 * 仓库
 * 缓冲区
 * wait()/notify()
 * @author DH
 *
 */
public class BufferArea {
    private int currNum = 0;//当前仓库的产品数量
    private int maxNum = 10;//仓库最大产品容量

    public synchronized void set(){
        if(currNum<maxNum){
            currNum++;
            System.out.println(Thread.currentThread().getName()+" 生产了一件产品！当前产品数为："+currNum);
            notifyAll();
        }else{//当前产品数大于仓库的最大容量
            try {
                System.out.println(Thread.currentThread().getName()+" 开始等待！当前仓库已满，产品数为："+currNum);
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void get(){
        if(currNum>0){//仓库中有产品
            currNum--;
            System.out.println(Thread.currentThread().getName()+" 获得了一件产品！当前产品数为："+currNum);
            notifyAll();
        }else{
            try {
                System.out.println(Thread.currentThread().getName()+" 开始等待！当前仓库为空，产品数为："+currNum);
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
