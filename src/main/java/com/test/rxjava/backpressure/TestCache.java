package com.test.rxjava.backpressure;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCache {

    public static void main(String[] args) throws InterruptedException {

        Iterator<Integer> iterator = new Iterator<Integer>() {

            AtomicInteger i = new AtomicInteger();

            public boolean hasNext() {
                return true;
            }

            public Integer next() {
                int product = i.getAndIncrement();
                System.out.println("Producer[" + Thread.currentThread().getName() + "]" + product);
                return product;
            }
        };
        ExecutorService executor = Executors.newFixedThreadPool(4);

        System.setProperty("rx2.buffer-size", "32");
        Flowable.fromIterable(() -> iterator)
                .flatMap(i ->
                        Flowable.just(i).observeOn(Schedulers.from(executor)).doOnNext(c -> {
                            System.out.println("Consumer[" + Thread.currentThread().getName() + "]" + c);
                            Thread.sleep(2000);
                        })

                ).blockingSubscribe();


    }

}
