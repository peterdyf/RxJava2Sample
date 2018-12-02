package com.test.rxjava.backpressure;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCatch {

    public static void main(String[] args) throws InterruptedException {

        Iterator<Integer> iterator = new Iterator<Integer>() {

            AtomicInteger i = new AtomicInteger();

            public boolean hasNext() {
                return true;
            }

            public Integer next() {

                int product = i.getAndIncrement();

                System.out.println("Producer[" + Thread.currentThread().getName() + "]" + product);

                int[] sleeps = new int[]{50, 150};

                try {
                    long sleep = sleeps[product % 2];
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return product;
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(1);





        Flowable.fromIterable(() -> iterator)

                .flatMap(i ->
                        Flowable.just(i).observeOn(Schedulers.from(executor)).doOnNext(c -> {
                            System.out.println("Consumer[" + Thread.currentThread().getName() + "]" + c);
                            Thread.sleep(100);
                        })

                ).blockingSubscribe();


    }

}
