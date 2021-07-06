#### 什么是Java虚拟机(Java Virtual Machine)？
> JVM是一个抽象的字节码运行时环境，Java能够跨平台特性也是得益于JVM虚拟机这种特性支持，JVM是一种规范，JVM定义了字节码文件的结构、字节码加载机制、运行时数据存储、自动内存管理、并发等内容。

#### JVM内存分布概述
在《Java虚拟机规范》中规定了五种JVM运行时数据区，分别是：程序计数器、虚拟机栈、本地方法栈、方法区、堆区。如下图所示：
![jvm.png](https://upload-images.jianshu.io/upload_images/5827906-0765c815d0baa236.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)

接下来，我们来对以上五个内存区域进行详细分析。

####  1 程序运行时，内存到底是如何进行分配的？
JVM在执行Java程序的过程中会把它所管理的内存划分为若干不同的数据区域，可能大多数人觉得在Java中内存分为堆(heap)内存和栈（stack）内存，大家下意识会觉得Java内存就只有heap和stack，这是不正确。

下面是通过一张图体现了`字节码`是如何被加载到这些区域来分析Java内存区域和这些区域分别存储了什么数据的，如图。
![jvm (1).png](https://upload-images.jianshu.io/upload_images/5827906-aa8fe1c0d06a9dcd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

* HelloWorld.java 文件首先需要经过javac编译器编译，生成HelloWorld.class字节码。

* 当Java程序中需要访问HelloWorld类时，需要经过ClassLoader 将 HelloWorld.class 字节码加载到JVM的内存区域中，字节码加载也称类加载。

* 字节码加载(类加载)指的是：`将.class文件中的二进制数据也就是字节码指令读入到内存中，将其放在运行时数据区的方法区内，然后在堆区创建一个 java.lang.Class 对象，用来封装该类在方法区内的数据结构`。
1、通过`类的全限定类名`来加载 `.class 文件的二进制字节流`；
2、将`.class 字节流`转换为`类的运行时数据结构`（存储在方法区）；
3、在 Java 堆中生成一个`类的 Class 对象`，它将作为方法区类数据的访问入口。
4、而 JVM 的程序运行，都是在栈上完成的，这和其他普通程序的执行是类似的，同样分为堆和栈。比如我们现在运行到了 main 方法，就会给它分配一个栈帧（Stack Frame）。当退出方法体时，会弹出相应的栈帧。你会发现，大多数`字节码指令，就是不断的对栈帧进行操作·。

* 类加载过程最终会在堆区(head)中产生全局唯一 `java.lang.Class对象`，Class对象封装了类在方法区内对应的数据结构，并且向Java程序员提供了访问方法区内的数据结构的接口，比如我们的反射就是通过Class对象，因为Class对象封装了类的信息和指针等，通过这些信息或指针访问类的方法或者字段等，需要注意的是Class是提供给开发人员访问方法区内的数据结构的接口，这和Java解析执行是不同的，Java解析执行是将内存中的直接码指令转换成机器码的过程，而在解析执行时需要通过Class拿到内存中字节码数据，如下图：
![c_java.png](https://upload-images.jianshu.io/upload_images/5827906-190c979a077ea5f6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

对比这张图可以看到 C++ 程序是编译成`操作系统`能够识别的` .exe `文件，而 `Java` 程序是`编译成 JVM 能够识别的 .class 文件`，然后由 JVM 负责调用系统函数执行程序。

在JVM中内存可以划分为：`程序计数器`,`虚拟机栈`,`本地方法栈`,`堆`,`方法区`。

#### 1、程序计数器（Program Counter Register）
>  * 程序计数器是一块较小的内存空间，它是`当前线程所执行的字节码的行号指示器，字节码解释器工作时通过改变该计数器的值来选择下一条需要执行的字节码指令，分支、跳转、循环`等基础功能都要依赖它来实现。每条线程都有一个独立的的程序计数器，各线程间的计数器互不影响，因此该区域是`线程私有`的。
> * 当线程在执行一个 Java 方法时，该计数器记录的是正在执行的虚拟机字节码指令的地址，当线程在执行的是 Native 方法（调用本地操作系统方法）时，该计数器的值为空。另外，该内存区域是唯一一个在 Java 虚拟机规范中么有规定任何 OOM（内存溢出：OutOfMemoryError）情况的区域。

为什么 JVM 需要这个程序计数器呢？
这是为了保证操作系统能够正确地进行线程切换，如：CPU时间片轮转机制会为每个线程分配时间片，当一个线程的时间片用完，或者其他线程提前抢夺 CPU 时间片时，当前线程就会挂起，而等到被挂起的线程再次得到时间片时，就需要通过程序计数器来恢复到正确的指令位置，确保程序能够从正确位置开始执行。

![cp.png](https://upload-images.jianshu.io/upload_images/5827906-768b61eb6a3828e6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如上图的，当线程1执行到cp = 1时挂起了，如果线程1重新获得时间片，那么字节码解释器从cp=1 改变该计数器的值来选择下一条需要执行的字节码指令，即 cp = 2，这就是图大概的意思。


总结以下几点：
* 程序计数器是线程私有的，每条线程都有一个程序计数器，互不影响。
* 程序计数器存放正在执行的虚拟机字节码指令的地址，字节码解释器通过给改变计数器的值来执行指令。
* 程序计数器是JVM内存中唯一没有规定OutMemoryError的区域。

#### 2、 虚拟机栈（Virtual Machine Stack）
> * 该区域也是`线程私有`的，它的生命周期也与线程相同。
> * 虚拟机栈描述的是 Java 方法执行的内存模型，每个方法被执行的时候都会同时创建一个`栈帧`，栈它是用于支持`虚拟机进行方法调用和方法执行的数据结构`。
> * 对于字节码执行引擎来讲，活动线程(Active Thread)中，`只有栈顶的栈帧是有效的，称为当前栈帧(Current stack frame)，这个栈帧所关联的方法称为当前方法，字节码执行引擎所运行的所有字节码指令都只针对当前栈帧进行操作`。
>* 栈帧用于存储`局部变量表、操作数栈、动态链接、方法返回地址和一些额外的附加信息`。`在编译程序代码时，栈帧中需要多大的局部变量表、多深的操作数栈都已经完全确定了，并且写入了方法表的 Code 属性之中`。因此，一个栈帧需要分配多少内存，不会受到程序运行期变量数据的影响，而仅仅取决于具体的虚拟机实现。

看一下字节码：
![VmStack.png](https://upload-images.jianshu.io/upload_images/5827906-bb4d634ac31a5a26.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)

栈帧中需要多大的局部变量表、多深的操作数栈都已经完全确定了，并且写入了方法表的 Code 属性之中。

1、JVM会给每个线程都分配一个私有的内存空间，称为Java虚拟机栈（Java VM Stack），Java虚拟机栈随着线程的创建而创建，JVM只会对其执行两种操作：`栈帧（Stack Frame）`的入栈和出栈。也就是说，Java虚拟机栈是存储栈帧的`后进先出`队列(LIFO)。

2、在 Java 虚拟机规范中，对这个区域规定了两种异常状况：
* StackOverflowError：当线程请求栈深度超出虚拟机栈所允许的深度时抛出，如：无条件结束递归。
* OutOfMemoryError：当 Java 虚拟机动态扩展到无法申请足够内存时抛出。

3、JVM 是基于栈的解释器执行的，DVM 是基于寄存器解释器执行的这里的`基于栈指的不是Java虚拟机栈，而是Java虚拟机栈帧中操作数栈`。虚拟机栈的初衷是用来描述 Java 方法执行的内存模型，每个方法被执行的时候，JVM 都会在虚拟机栈中创建一个`栈帧（Stack Frame）`。

4、字节码的执行流程是什么？字节码经过`加载（将外部的 .class 文件，加载到 Java 的方法区内）、验证（验证字节码文件结构，不符合规范的将抛出 java.lang.VerifyError 错误）、准备（类变量分配内存，并将其初始化为默认值）、解析（将符号引用替换为直接引用的过程）`以及初始化（初始化成员变量）。在Java虚拟机栈中被执行，每一项内容都可以看作是一个栈帧，栈帧的结构包括局部变量表、操作数栈、链接、返回地址。这时候就很明了了，`栈帧的执行流程就是字节码的执行流程`。方法中的变量会被解析到局部变量表，然后对操作数栈进行入栈出栈的操作，在此期间有可能引用到动态或静态链接，最后把计算结果的引用地址返回。

5、JVM 是基于栈(操作数栈)的体系结构来执行 class 字节码的。线程创建后，都会产生程序计数器（PC）和栈（Stack），程序计数器存放下一条要执行的指令在方法内的偏移量，栈中存放一个个栈帧，每个栈帧对应着每个方法的每次调用，而栈帧又是由局部变量和操作数栈两部分组成，局部变量区用于存放方法中的局部变量和参数，操作数栈中用于存放方法执行过程中产生的中间结果。

##### 2.1、栈帧（Stack Frame）
> 栈帧（Stack Frame）是用于支持虚拟机进行方法调用和方法执行的数据结构，每一个线程在执行某个方法时，都会为这个方法创建一个栈帧。每个方法在执行过程中，都会伴随着栈帧的创建、入栈和出栈。栈帧是用来存储局部数据和部分过程结果的数据结构，主要包含 局部变量表（Local Variable Table）、操作数栈（Operand Stack）、动态连接（指向当前方法所属类的运行时常量池的引用）、返回地址，如图所示为Java虚拟机栈的模型。

![jvm stack.png](https://upload-images.jianshu.io/upload_images/5827906-e680c41c12ae93f1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)

#####  2.1.1 、局部变量表
 局部变量表是一组`变量值`存储空间，主要用于存储`方法参数和方法内部定义的局部变量`，其中存储的数据的类型是编译期可知的各种基本数据类型、对象引用（reference）和 returnAddress 类型（它指向了一条字节码指令的地址）。
局部变量表所需的内存空间在编译期间完成分配，即在Java源代码被编译成 Class 文件时，就确定了所需分配的最大局部变量表的容量。当进入一个方法时，这个方法需要在栈中分配多大的局部变量空间是完全确定的，在方法运行期间不会改变局部变量表的大小，如下代码所示：

    public class VMStack {
        public int add(int i, int j) {
        int result = 3;
        result = i + j;
        return result + 100;
      }
        public static int staticAdd(int i, int j) {
        int result = 3;
        result = i + j;
        return result + 100;
      }
    }
使用 javap -v 反编译之后，得到如下字节码指令：
![LVB.png](https://upload-images.jianshu.io/upload_images/5827906-ee5014fdac5c9917.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


上面的 locals=4 代表局部变量表长度是 4，也就是说经过编译之后，局部变量表的长度已经确定为4，分别保存：参数 i、j和局部变量 result。而stack=2 表示操作数栈的最大深度为 2，而args_size = 3 表示方法参数个数是3（反射调用静态方法和实例方法的区别）。

局部变量表的容量以变量槽（Slot）为最小单位，虚拟机通过索引定位的方式使用局部变量表，索引值的范围是从 0 开始到局部变量表最大的 Slot 数量，`在方法执行时，虚拟机是使用局部变量表来完成参数值到参数变量列表的传递过程的，如果是实例方法（非static），则局部变量表中的第 0 位索引的 Slot 默认是用于传递方法所属对象实例的引用，在方法中可以通过关键字 this 来访问这个隐含的参数。其余参数则按照参数表的顺序来排列，占用从1开始的局部变量 Slot，参数表分配完毕后，再根据方法体内部定义的变量顺序和作用域分配其余的 Slot`。

需要注意的是，JVM在类加载的时候不会为局部变量赋予初始值（实例变量和类变量都会被赋予初始值）

#####  2.1.2 、操作数栈
* 操作数栈又常被称为操作栈，操作数栈的最大深度(stack)也是在编译的时候就确定了。当一个方法开始执行时，它的操作栈是空的，在方法的执行过程中，会有各种字节码指令（比如：加操作、赋值元算等）向操作栈中写入和提取内容，也就是入栈和出栈操作。

* Java 虚拟机的解释执行引擎称为“基于栈的执行引擎”，其中所指的“栈”就是操作数栈。因此我们也称 Java 虚拟机是基于栈的，这点不同于 Android 虚拟机，Android 虚拟机是基于寄存器的。

* 基于栈的指令集最主要的优点是可移植性强，主要的缺点是执行速度相对会慢些；而由于寄存器由硬件直接提供，所以基于寄存器指令集最主要的优点是执行速度快，主要的缺点是可移植性差。

#####  2.1.3 、动态连接
每个栈帧都包含一个指向运行时常量池（方法区）中该栈帧所属方法的引用，持有这个引用是为了支持方法调用过程中的动态连接。Class 文件的常量池中存在有大量的符号引用，字节码中的方法调用指令就以常量池中指向方法的符号引用为参数。这些符号引用，一部分会在类加载阶段或第一次使用的时候转化为直接引用（如 final、static 域等），称为静态解析，另一部分将在每一次的运行期间转化为直接引用，这部分称为动态连接。

#####  2.1.4、方法返回地址
当一个方法被执行后，有两种方式退出该方法：执行引擎遇到了任意一个方法返回的`字节码指令`或遇到了`异常`，并且该异常没有在方法体内得到处理。

无论采用何种退出方式，在方法退出之后，都需要返回到方法被调用的位置，程序才能继续往下执行。方法返回时可能需要在栈帧中保存一些信息，用来帮助恢复它的上层方法的执行状态。一般来说，方法正常退出时，调用者的 PC 计数器的值就可以作为返回地址，栈帧中很可能保存了这个计数器值，而方法异常退出时，返回地址是要通过异常处理器来确定的，栈帧中一般不会保存这部分信息。

方法退出的过程实际上等同于把当前栈帧出站，因此退出时可能执行的操作有：恢复上层方法的局部变量表和操作数栈，如果有返回值，则把它压入调用者栈帧的操作数栈中，调整 PC 计数器的值以指向方法调用指令后面的一条指令。

最后把虚拟机张举个例子：

     public int add(int i, int j) {
        int result = 3;
        result = i + j;
        return result + 100;
    }

使用 javap -v 反编译之后，得到如下字节码指令：

     public int add(int, int);
        descriptor: (II)I
        flags: ACC_PUBLIC
        Code:
          stack=2, locals=4, args_size=3
             0: iconst_3      (把常量 3 压入操作数栈栈顶)
             1: istore_3      (把操作数栈栈顶的出栈放入局部变量表索引为 1 的位置)
             2: iload_1       (把局部变量表索引为 1 的值放入操作数栈栈顶)
             3: iload_2       (把局部变量表索引为 2 的值放入操作数栈栈顶)
             4: iadd          (将操作数栈栈顶的和栈顶下面的一个进行加法运算后放入栈顶)
             5: istore_3      (把操作数栈栈顶的出栈放入局部变量表索引为 3 的位置)
             6: iload_3        (把局部变量表索引为 3 的值放入操作数栈栈顶)
             7: bipush  100    (把常量 100 压入操作数栈栈顶)
             9: iadd           (将操作数栈栈顶的和栈顶下面的一个进行加法运算后放入栈顶)
            10: ireturn        (结束)
* iconst 和 bipush指令是将常量压入操作数栈顶，区别就是：当 int 取值 -1~5 采用 iconst 指令，取值 -128~127 采用 bipush 指令。

* istore 将操作数栈顶的元素放入局部变量表的某索引位置，比如 istore_3 代表将操作数栈顶元素放入局部变量表下标为3 的位置。

* iload 将局部变量表中某下标上的值加载到操作数栈顶中，比如 iload_2 代表将局部变量表索引为 2 上的值压入操作数栈顶。

* iadd 代表加法运算，具体是将操作数栈最上方的两个元素进行相加操作，然后将结果重新压入栈顶。






































































