package com.project.bookstudy.study_group.service;


import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class ThreadTest {

    @Test
    void runnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread: " + Thread.currentThread().getName());
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        System.out.println("Hello: " + Thread.currentThread().getName());
    }

    @Test
    void threadStart() {
        Thread thread = new MyThread();

        thread.start();
        System.out.println("Hello: " + Thread.currentThread().getName());
    }

    @Test
    void threadRun() {
        Thread thread = new MyThread();

        thread.run();
        thread.run();
        thread.run();
        System.out.println("Hello: " + Thread.currentThread().getName());
    }


    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Thread: " + Thread.currentThread().getName());
        }
    }

    @Test
    void callable_void() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() {
                final String result = "Thread: " + Thread.currentThread().getName();
                System.out.println(result);
                return null;
            }
        };

        executorService.submit(callable);
        executorService.shutdown();
    }


    @Test
    void callable_String() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() {
                return "Thread: " + Thread.currentThread().getName();
            }
        };

        executorService.submit(callable);
        executorService.shutdown();
    }

    @Test
    void get() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<String> callable = callable();

        // It takes 3 seconds by blocking(블로킹에 의해 3초 걸림)
        Future<String> future = executorService.submit(callable);

        System.out.println(future.get());

        executorService.shutdown();
    }

    private Callable<String> callable() {
        return new Callable<String>() {
            @Override
            public String call() throws InterruptedException {
                Thread.sleep(3000L);
                return "Thread: " + Thread.currentThread().getName();
            }
        };
    }

}
