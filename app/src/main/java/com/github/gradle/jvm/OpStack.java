package com.github.gradle.jvm;

public class OpStack {
    public int add(int i, int j) {
        //         0: ldc           #2                  // String yuyuyuoooo
        //         2: astore_3
        //         3: new           #3                  // class java/lang/StringBuilder
        //         6: dup
        //         7: invokespecial #4                  // Method java/lang/StringBuilder."<init>":()V
        //        10: aload_3
        //        11: invokevirtual #5                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //        14: ldc           #6                  // String bbbb
        //        16: invokevirtual #5                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //        19: invokevirtual #7                  // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
        //        22: astore        4
        String abc = "yuyuyu" + "oooo";// 这行代码 编译器会在编译器优化  常量池直接 yuyuyuoooo
        // 编译会创建 StringBuilder
        String abcd = abc + "bbbb"; // 编译器会创建 StringBuilder对象并调用append方法
        int result = 3;
        result = i + j;
        return result + 100;
    }

    public void add2() {
        int a = 8;
        int b = 6;
        add(a, b);
    }
}

