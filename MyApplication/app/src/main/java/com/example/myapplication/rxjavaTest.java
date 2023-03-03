package com.example.myapplication;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class rxjavaTest {
    static final String TAG = rxjavaTest.class.getSimpleName();
    /**
     * 最基本的流程：
     * 订阅 -> 观察者onSubscribe -> 被观察者subscribe(ObservableEmitter<Integer> emitter)
     */
    public void rxjavaTestNormal(){
        // 1、创建被观察者Observable
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            /**
             * 被观察者Observable的subscribe中会使用ObservableEmitter发送事件，观察者响应对应的事件
             */
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                Log.d(TAG, "subscribe: ");
            }
        });


        // 2、创建观察者Observer
        Observer<Integer> observer = new Observer<Integer>() {

            /**
             * 观察者接收事件前，默认最先调用复写 onSubscribe()
             */
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: "+d.isDisposed());
            }

            /**
             * 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件作出响应" + value);
            }

            /**
             * 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            /**
             * 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };

        // 3、当 Observable 被订阅后，观察者的Observer的OnSubscribe方法会自动被调用，被观察者Observable的subscribe方法会被调用
        observable.subscribe(observer);
    }
    /**
     * 最基本的流程+发送数据
     */
    public void rxjavaNormalAndSend(){
        // 1、创建被观察者Observable
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            /**
             * 被观察者Observable的subscribe中会使用ObservableEmitter发送事件，观察者响应对应的事件
             */
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                Log.d(TAG, "subscribe: ");
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
                emitter.onNext(4);
                emitter.onNext(5);
            }
        });


        // 2、创建观察者Observer
        Observer<Integer> observer = new Observer<Integer>() {

            /**
             * 观察者接收事件前，默认最先调用复写 onSubscribe()
             */
            @Override
            public void onSubscribe(Disposable d) {
                //onSubscribe是最先被调用的
                //Disposable相对于阀门，调用这个dispose()方法，后面的方法都不会被调用
//                d.dispose();
                Log.d(TAG, "onSubscribe: "+d.isDisposed());
            }

            /**
             * 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件作出响应" + value);
            }

            /**
             * 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            /**
             * 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };

        // 3、当 Observable 被订阅后，观察者的Observer的OnSubscribe方法会自动被调用，被观察者Observable的subscribe方法会被调用
        observable.subscribe(observer);
    }

    /**
     * 最基本的流程+批量发送数据
     */
    public void rxjavaNormalAndJustsend(){
        // 1、创建被观察者Observable
        Observable<Integer> observable = Observable.just(1,2,3);


        // 2、创建观察者Observer
        Observer<Integer> observer = new Observer<Integer>() {

            /**
             * 观察者接收事件前，默认最先调用复写 onSubscribe()
             */
            @Override
            public void onSubscribe(Disposable d) {
                //onSubscribe是最先被调用的
                //Disposable相对于阀门，调用这个dispose()方法，后面的方法都不会被调用
//                d.dispose();
                Log.d(TAG, "onSubscribe: "+d.isDisposed());
            }

            /**
             * 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件作出响应" + value);
            }

            /**
             * 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            /**
             * 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };

        // 3、当 Observable 被订阅后，观察者的Observer的OnSubscribe方法会自动被调用，被观察者Observable的subscribe方法会被调用
        observable.subscribe(observer);
    }

    /**
     * 最基本的流程+批量发送数据+过滤
     */
    public void rxjavaNormalAndJustsendAndfilter(){
        // 1、创建被观察者Observable
        Observable<Integer> observable = Observable.just(1,2,3).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer value) throws Exception {
                return value % 2 == 1;
            }
        });



        // 2、创建观察者Observer
        Observer<Integer> observer = new Observer<Integer>() {

            /**
             * 观察者接收事件前，默认最先调用复写 onSubscribe()
             */
            @Override
            public void onSubscribe(Disposable d) {
                //onSubscribe是最先被调用的
                //Disposable相对于阀门，调用这个dispose()方法，后面的方法都不会被调用
//                d.dispose();
                Log.d(TAG, "onSubscribe: "+d.isDisposed());
            }

            /**
             * 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件作出响应" + value);
            }

            /**
             * 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            /**
             * 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };

        // 3、当 Observable 被订阅后，观察者的Observer的OnSubscribe方法会自动被调用，被观察者Observable的subscribe方法会被调用
        observable.subscribe(observer);
    }

    /**
     * 最基本的流程+线程切换
     */
    public void rxjavaNormalThread(){
        // 1、创建被观察者Observable
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "subscribe: Thread:"+Thread.currentThread());
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        });



        // 2、创建观察者Observer
        Observer<Integer> observer = new Observer<Integer>() {

            /**
             * 观察者接收事件前，默认最先调用复写 onSubscribe()
             */
            @Override
            public void onSubscribe(Disposable d) {
                //onSubscribe是最先被调用的
                //Disposable相对于阀门，调用这个dispose()方法，后面的方法都不会被调用
//                d.dispose();
                Log.d(TAG, "onSubscribe: "+d.isDisposed()+" :"+Thread.currentThread());
            }

            /**
             * 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件作出响应" + value+" :"+Thread.currentThread());
            }

            /**
             * 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            /**
             * 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
             */
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应"+" :"+Thread.currentThread());
            }
        };

        // 3、当 Observable 被订阅后，观察者的Observer的OnSubscribe方法会自动被调用，被观察者Observable的subscribe方法会被调用
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
