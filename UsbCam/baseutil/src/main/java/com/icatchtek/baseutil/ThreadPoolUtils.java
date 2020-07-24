package com.icatchtek.baseutil;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author b.jiang
 * @date 2019/3/12
 * @description
 * 线程任务类,用来管理程序中出现的所有线程
 */
public class ThreadPoolUtils {
    /**
     * 线程任务的实例
     */
    private static ThreadPoolUtils instance;

    /**
     * 网络线程最大数量
     */
    private final int netThreadCount = 5;

    /**
     * 数据库线程最大数量
     */
    private final int dbThreadCount = 3;

    /**
     * 其他类型的耗时线程数量
     */
    private final int otherThreadCount = 10;


    /**
     * 延时线程数量
     */
    private final int delayThreadCount = 3;

    /**
     * 数据库线程池
     */
    private ThreadPoolExecutor dbThreadPool;

    /**
     * 网络线程池
     */
    private ThreadPoolExecutor netThreadPool;

    /**
     * 其他耗时操作线程池
     */
    private ThreadPoolExecutor otherThreadPool;

    /**
     * 延时任务线程池
     */
    private ScheduledExecutorService scheduleExec;
    //org.apache.commons.lang3.concurrent.BasicThreadFactory


    /**
     * 数据库线程队列
     */
    private PriorityBlockingQueue dbThreadQueue;

    /**
     * 网络线程队列
     */
    private PriorityBlockingQueue netThreadQueue;

    /**
     * 其他线程队列
     */
    private PriorityBlockingQueue otherThreadQueue;

    /**
     * 其他线程队列
     */
    private PriorityBlockingQueue delayThreadQueue;

    /**
     * 任务比较
     */
    private Comparator<PrioriTask> taskCompare;

    private ThreadPoolUtils() {
        final long keepAliveTime = 60L;
        taskCompare = new TaskCompare();
        dbThreadQueue = new PriorityBlockingQueue<PrioriTask>(dbThreadCount, taskCompare);
        netThreadQueue = new PriorityBlockingQueue<PrioriTask>(netThreadCount, taskCompare);
        otherThreadQueue = new PriorityBlockingQueue<PrioriTask>(dbThreadCount, taskCompare);
        delayThreadQueue = new PriorityBlockingQueue<PrioriTask>(dbThreadCount, taskCompare);

        dbThreadPool = new ThreadPoolExecutor(dbThreadCount, dbThreadCount, 0L, TimeUnit.MILLISECONDS, dbThreadQueue);
        netThreadPool = new ThreadPoolExecutor(netThreadCount, netThreadCount, 0L, TimeUnit.MILLISECONDS, netThreadQueue);
        otherThreadPool = new ThreadPoolExecutor(otherThreadCount, Integer.MAX_VALUE, keepAliveTime, TimeUnit.SECONDS, otherThreadQueue);
        scheduleExec =  new ScheduledThreadPoolExecutor(delayThreadCount);
    }

    /**
     * 获取线程管理实例
     *
     * @return 线程管理实例
     */
    public static ThreadPoolUtils getInstance() {
        if (instance == null) {
            instance = new ThreadPoolUtils();
        }
        return instance;
    }

    /**
     * 获取网络线程池
     *
     * @return
     */
    public ThreadPoolExecutor getNetThreadPool() {
        return netThreadPool;
    }

    /**
     * 执行数据库线程
     *
     * @param task     需要执行的任务
     * @param priority 优先级 {@link ThreadPeriod}
     */
    public void executorDBThread(Runnable task, int priority) {
        if (!dbThreadPool.isShutdown()) {
            dbThreadPool.execute(new PrioriTask(priority, task));
        }
    }

    /**
     * 执行网络线程
     *
     * @param task     需要执行的任务
     * @param priority {@link ThreadPeriod} 优先级
     */
    public void executorNetThread(Runnable task, int priority) {

        if (!netThreadPool.isShutdown()) {
            netThreadPool.execute(new PrioriTask(priority, task));
        }
    }

    /**
     * 执行除数据库之外的其他耗时任务
     *
     * @param task     需要执行的任务
     * @param priority {@link ThreadPeriod} 优先级
     */
    public void executorOtherThread(Runnable task, int priority) {
        if (!otherThreadPool.isShutdown()) {
            otherThreadPool.execute(new PrioriTask(priority, task));
        }
    }

    /**
     * 结束掉所有线程,并且回收（正在进行的有可能结束不掉）
     */
    public void shutDownAll() {
        netThreadPool.shutdownNow();
        dbThreadPool.shutdownNow();
        otherThreadPool.shutdownNow();
        instance = null;
    }

    /**
     * 优先级任务
     *
     * @author RES-KUNZHU
     */
    public class PrioriTask implements Runnable {
        private int priori;

        private Runnable task;

        /**
         * Cnstructor Method。
         *
         * @param priority 优先级
         * @param runnable 线程
         */
        public PrioriTask(int priority, Runnable runnable) {
            priori = priority;
            task = runnable;
        }

        public int getPriori() {
            return priori;
        }

        public void setPriori(int priori) {
            this.priori = priori;
        }

        public Runnable getTask() {
            return task;
        }

        public void setTask(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            if (task != null) {
                task.run();
            }
        }

    }

    /**
     * 任务比较器
     *
     * @author RES-KUNZHU
     */
    public class TaskCompare implements Comparator<PrioriTask> {

        @Override
        public int compare(PrioriTask lhs, PrioriTask rhs) {
            return rhs.getPriori() - lhs.getPriori();
        }
    }

    /**
     * 线程优先级
     *
     * @author RES-KUNZHU
     */
    public static class ThreadPeriod {
        /**
         * 线程优先级 低
         */
        public static final int PERIOD_LOW = 1;

        /**
         * 线程优先级 中
         */
        public static final int PERIOD_MIDDLE = 5;

        /**
         * 线程优先级 高
         */
        public static final int PERIOD_HIGHT = 10;
    }


    /**
     * 延迟执行Runnable命令
     *
     * @param command 命令
     * @param delay   延迟时间
     * @param unit    单位
     * @return 表示挂起任务完成的ScheduledFuture，并且其{@code get()}方法在完成后将返回{@code null}
     */
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return scheduleExec.schedule(command, delay, unit);
    }

    /**
     * 延迟执行Callable命令
     *
     * @param callable 命令
     * @param delay    延迟时间
     * @param unit     时间单位
     * @param <V>      泛型
     * @return 可用于提取结果或取消的ScheduledFuture
     */
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return scheduleExec.schedule(callable, delay, unit);
    }

    /**
     * 延迟并循环执行命令
     *
     * @param command      命令
     * @param initialDelay 首次执行的延迟时间
     * @param period       连续执行之间的周期
     * @param unit         时间单位
     * @return 表示挂起任务完成的ScheduledFuture，并且其{@code get()}方法在取消后将抛出异常
     */
    public ScheduledFuture<?> scheduleWithFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduleExec.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * 延迟并以固定休息时间循环执行命令
     *
     * @param command      命令
     * @param initialDelay 首次执行的延迟时间
     * @param delay        每一次执行终止和下一次执行开始之间的延迟
     * @param unit         时间单位
     * @return 表示挂起任务完成的ScheduledFuture，并且其{@code get()}方法在取消后将抛出异常
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return scheduleExec.scheduleWithFixedDelay(command, initialDelay, delay, unit);

    }
}
