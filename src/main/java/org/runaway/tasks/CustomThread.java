package org.runaway.tasks;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CustomThread {
    private static final List<CustomThread> threads = new ArrayList<>();
    private static int threadsValue = 0;

    public ExecutorService getThread() {
        return thread;
    }

    private ExecutorService thread;

    public CustomThread(int threads) {
        this.thread = Executors.newFixedThreadPool(threads);
        CustomThread.threads.add(this);
        threadsValue++;
    }

    public CustomThread() {
        new CustomThread("Custom cached thread");
    }

    public CustomThread(String name) {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(name)
                .setDaemon(true)
                .build();
        this.thread = Executors.newCachedThreadPool(threadFactory);
        threads.add(this);
        threadsValue++;
    }

    public CustomThread(int threads, String name) {
        if (threads == 0) {
            new CustomThread(name);
        }
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(name)
                .setDaemon(true)
                .build();
        this.thread = Executors.newFixedThreadPool(threads, threadFactory);
        CustomThread.threads.add(this);
        threadsValue++;
    }

    public static void shutdownAll() {
        threads.forEach(thread -> thread.getThread().shutdown());
    }

    public void run(Runnable run) {
        thread.submit(run);
    }

    public Future task(Callable task) {
        return thread.submit(task);
    }

    public void stop() {
        this.thread.shutdown();
        threadsValue--;
    }
}
