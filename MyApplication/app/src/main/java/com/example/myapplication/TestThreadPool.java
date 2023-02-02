package com.example.myapplication;

import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestThreadPool {
    static String rtag = "RongCloudRTC";
    static String rtagnum = "1234567890";
    static String rUserId = "300";
    //存储用户ID
    static HashMap<String, String> userIds = new HashMap<>();
    static HashMap<String, String> userTags = new HashMap<>();
    private static final Pattern PATTERN_NUMERIC = Pattern.compile("[0-9]*");
    public static boolean isNumeric(String str) {
        Matcher isNum = PATTERN_NUMERIC.matcher(str);
        return isNum.matches();
    }
    /**
     * 判断是否是int数值
     *
     * @param str
     * @return
     */
    public static boolean isNumTag(String str) {
        if(null==str || str.length()==0){
            return false;
        }
        if (isNumeric(str)) {
            return true;
        } else{
            return false;
        }

//        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
//        return pattern.matcher(str).matches();
    }
    public static boolean isEmptyString(String str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    public static void main (String[] args) throws InterruptedException, ExecutionException {
//        testCallable();
//        testThreadPoolExecutor();
//        Boolean b = isNumTag("3001234561");
//        System.out.println(" b result:" + b);

//            userTags.put(rUserId,rtagnum);
        System.out.println(Thread.currentThread().getName() + " main");

        try {
            testThreadPoolExecutor();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception:"+e);
        }
    }



    public static void testCallable() throws ExecutionException, InterruptedException {
        // 创建Callable
        MyCallable call = new MyCallable(10);
        // 创建任务
        FutureTask<Integer> task = new FutureTask<Integer>(call);
        // 新建线程，设置任务
        Thread thread = new Thread(task);
        // 启动子线程
        thread.start();
        long begin = System.currentTimeMillis();
        System.out.println("已启动子线程");
        // 调用get方法会阻塞用户线程，如不调用，用户线程会继续往下执行
        Integer sum = task.get();
        System.out.println("结果" + sum + ",耗时:" + (System.currentTimeMillis() - begin) / 1000);
    }
    /**
     * 线程安全的队列
     */
    static Queue<String> queue = new ConcurrentLinkedQueue<String>();

    static {
        //入队列
        for (int i = 0; i < 9; i++) {
            queue.add("task-" + i);
        }
    }

    public static void testThreadPoolExecutor() throws Exception {

        //~~~~~~~~~~~~~~~~~~~~~~~~~
        //基础参数
        int corePoolSize=2;//最小活跃线程数
        int maximumPoolSize=2;//最大活跃线程数
        int keepAliveTime=5;//指定线程池中线程空闲超过 5s 后将被回收
        TimeUnit unit = TimeUnit.SECONDS;//keepAliveTime 单位
        //阻塞队列
        BlockingQueue<Runnable> workQueue = null;
        //1 有界队列
        workQueue = new ArrayBlockingQueue<>(5);//基于数组的先进先出（FIFO）队列，支持公平锁和非公平锁，有界
        workQueue = new LinkedBlockingQueue<>();//基于链表的先进先出（FIFO）队列，默认长度为 Integer.MaxValue 有OOM危险，有界

        //2 无界队列
        workQueue = new PriorityBlockingQueue(); //支持优先级排序的无限队列，默认自然排序，可以实现 compareTo()方法指定排序规则，不能保证同优先级元素的顺序，无界。
        workQueue = new DelayQueue(); //一个使用优先级队列（PriorityQueue）实现的无界延时队列，在创建时可以指定多久才能从队列中获取当前元素。只有延时期满后才能从队列中获取元素。
        workQueue = new LinkedTransferQueue(); //一个由链表结构组成的,无界阻塞队列
        //3 同步移交队列
        workQueue = new SynchronousQueue<>();//无缓冲的等待队列，队列不存元素，每个put操作必须等待take操作，否则无法添加元素，支持公平非公平锁，无界
//        workQueue = new LinkedBlockingDeque(4); //一个由链表结构组成的,双向阻塞队列，有界
        //拒绝策略
        RejectedExecutionHandler rejected = null;
        rejected = new ThreadPoolExecutor.AbortPolicy();//默认，队列满了丢任务抛出异常
        rejected = new ThreadPoolExecutor.DiscardPolicy();//队列满了丢任务不异常
        rejected = new ThreadPoolExecutor.DiscardOldestPolicy();//将最早进入队列的任务删，之后再尝试加入队列
        rejected = new ThreadPoolExecutor.CallerRunsPolicy();//如果添加到线程池失败，那么主线程会自己去执行该任务


        //使用的线程池
        ExecutorService threadPool = null;
//        threadPool = Executors.newCachedThreadPool();//有缓冲的线程池，线程数 JVM 控制
//        threadPool = Executors.newFixedThreadPool(3);//固定大小的线程池
//        threadPool = Executors.newScheduledThreadPool(2);
//        threadPool = Executors.newSingleThreadExecutor();//单线程的线程池，只有一个线程在工作
        threadPool = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                workQueue,
                rejected);//默认线程池，可控制参数比较多
        //执行无返回值线程
        TaskRunnable taskRunnable;
        taskRunnable = new TaskRunnable(1);
        threadPool.execute(taskRunnable);
        System.out.println("阻塞队列长度：" + workQueue.size());
        taskRunnable = new TaskRunnable(2);
        threadPool.execute(taskRunnable);
        System.out.println("阻塞队列长度：" + workQueue.size());
        taskRunnable = new TaskRunnable(3);
        threadPool.execute(taskRunnable);
        System.out.println("阻塞队列长度：" + workQueue.size());
        taskRunnable = new TaskRunnable(4);
        threadPool.execute(taskRunnable);
        System.out.println("阻塞队列长度：" + workQueue.size());
        taskRunnable = new TaskRunnable(5);
        threadPool.execute(taskRunnable);
        System.out.println("阻塞队列长度：" + workQueue.size());
        taskRunnable = new TaskRunnable(6);
        threadPool.execute(taskRunnable);
        System.out.println("阻塞队列长度：" + workQueue.size());
        taskRunnable = new TaskRunnable(7);
        threadPool.execute(taskRunnable);
        System.out.println("阻塞队列长度：" + workQueue.size());
//        List<Future<String>> futres = new ArrayList<>();
//        for(int i=0;i<10;i++) {
//            //执行有返回值线程
//            TaskCallable taskCallable = new TaskCallable(i);
//            Future<String> future = threadPool.submit(taskCallable);
//            futres.add(future);
//        }
//        for(int i=0;i<futres.size();i++){
//            String result = futres.get(i).get();
//            System.out.println(i+" result = "+result);
//        }
    }
    /**
     * 返回值的线程，使用 threadpool.execut() 执行
     */
    public static class TaskRunnable implements Runnable{
        int mNum;
        TaskRunnable(int num){
            mNum = num;
        }
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " runnable result! mNum:"+mNum);
        }
    }
    /**
     * 有返回值的线程，使用 threadpool.submit() 执行
     */
    public static class TaskCallable implements Callable<String> {
        public TaskCallable(int index){
            this.i=index;
        }
        private int i;
        @Override
        public String call() throws Exception {
            int r = new Random().nextInt(5);
            try {
                Thread.sleep(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("callable result!");
            return Thread.currentThread().getName()+" callable index="+i +",sleep="+r;
        }
    }
}
