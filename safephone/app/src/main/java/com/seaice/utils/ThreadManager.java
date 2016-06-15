package com.seaice.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by seaice on 2016/6/13.
 */
public class ThreadManager {

    private static ThreadPool threadPool;

    public static ThreadPool getThreadPool() {
        if (threadPool == null) {
            synchronized (ThreadManager.class) {
                if (threadPool == null) {
                    int corePoolSize = 5;
                    int maxPoolSize = 10;
                    long keepAliveTime = 0L;
                    threadPool = new ThreadPool(corePoolSize, maxPoolSize, keepAliveTime);
                }
            }
        }
        return threadPool;
    }

    public static class ThreadPool {
        public static ThreadPoolExecutor executor = null;

        private int corePoolSize;
        private int maxPoolSize;
        private long keepAliveTime = 0;

        public ThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maxPoolSize = maxPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        public void execute(Runnable runnable) {
            if (runnable == null) {
                return;
            }

            if (executor == null) {
                executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                            new LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy());
            }
            executor.execute(runnable);
        }
    }
}
