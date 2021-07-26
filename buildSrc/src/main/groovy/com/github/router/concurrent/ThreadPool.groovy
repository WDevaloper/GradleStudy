package com.github.router.concurrent

import org.gradle.api.Action

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


class ThreadPool {
    // WaitableExecutor globalSharedThreadPool = WaitableExecutor.useGlobalSharedThreadPool()
    // 等待所有任务结束
    // globalSharedThreadPool.waitForTasksWithQuickFail(true)

    private ScheduledExecutorService globalSharedThreadPool =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1)

    private List<ITask> taskList = new ArrayList<>()


    void addTask(ITask task) {
        taskList.add(task)
    }


    void startWork() {
        // 堵塞等待所有的任务执行完成后统一返回
        globalSharedThreadPool.invokeAll(taskList)
        taskList.clear()
    }
}