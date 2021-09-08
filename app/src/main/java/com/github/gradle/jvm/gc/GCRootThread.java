package com.github.gradle.jvm.gc;


import static com.github.gradle.jvm.gc.Util.printMemory;

public class GCRootThread {
    private int _10MB = 10 * 1024 * 1024;
    private byte[] memory = new byte[8 * _10MB];

    public static void main(String[] args) throws Exception {
        System.out.println("开始前内存情况:");
        printMemory();
        AsyncTask at = new AsyncTask(new GCRootThread());
        Thread thread = new Thread(at);
        thread.start();

        at = null; // 并没
        // 有会回收掉，所以证明 验证活跃线程可以作为GC Root
        System.gc();
        System.out.println("main方法执行完毕，完成GC");
        printMemory();

        thread.join();
        at = null;
        System.gc();
        System.out.println("线程代码执行完毕，完成GC");
        printMemory();
    }

    private static class AsyncTask implements Runnable {
        private GCRootThread gcRootThread;

        public AsyncTask(GCRootThread gcRootThread) {
            this.gcRootThread = gcRootThread;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
    }
}
