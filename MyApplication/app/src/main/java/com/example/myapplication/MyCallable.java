package com.example.myapplication;

//public class MyCallable {
//}

import java.util.concurrent.Callable;

class MyCallable implements Callable<Integer> {
    private int num;

    public MyCallable(int num) {
        this.num = num;
    }

    @Override
    public Integer call() throws Exception {
        // 从 1 到 num 的合计
        int sum = 0;
        for (int i = 1; i <= num; i++) {
            Thread.sleep(1000);
            sum += i;
        }
        return sum;
    }
}
