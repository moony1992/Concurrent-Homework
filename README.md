
多线程的原理分析(1)
====

```java
    public class SynchronizedDemo implements Runnable{
    int x = 100;

    public synchronized void m1() {
        x = 1000;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("x=" + x);
    }

    public synchronized void m2() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        x = 2000;
    }
	public static void main(String[] args) throws InterruptedException {
        SynchronizedDemo sd = new SynchronizedDemo();
        new Thread(()->sd.m1()).start();
        new Thread(()->sd.m2()).start();
        sd.m2();
        System.out.println("Main x=" + sd.x);
    }
    @Override
    public void run() {
        m1();
    }
}
```
1
x=1000 Main x=2000

Main x=2000 x=1000

Main x=1000 x=1000

多线程获得锁的顺序不可控
因进入m1的线程TIME_WAITTING时间比m2的明显较久，再加上m2的锁可上升到重量级锁，不可能出现，m1打印x=2000的情况
```java
public class SynchronizedDemo  {
   static Integer count=0;
   public static void incr(){
       synchronized (count) {
           try {
               Thread.sleep(1);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           count++;
       }
   }
    public static void main(String[] args) throws IOException, InterruptedException {
        for(int i=0;i<1000;i++){
            new Thread(()->SynchronizedDemo.incr()).start();
        }
        Thread.sleep(5000);
        System.out.println("result:"+count);
    }
}
```

2 数据随机小于1000
锁的是count对象，count++后是重新建一个Integer类型去赋值，锁的不是同一个对象无意义

多线程的原理分析(2)
====

请列出Happens-before的几种规则
----

volatile 变量规则
传递性规则
start 规则
join 规则
监视器锁的规则

volatile 能使得一个非原子操作变成原子操作吗？为什么？
----

不能，volatile只能解决可见性及顺序一致性问题；

哪些场景适合使用Volatile
----

多线程间需要对共享变量具有可见性时。

如果对一个数组修饰volatile，是否能够保证数组元素的修改对其他线程的可见？为什么？
----

不能，数组是对成员变量或对象一个地址引用，volatile可保证对于对象数组的地址具有可见性，但是数组或对象内部的成员变量不具有可见性。

AQS的底层原理分析
----

![]
(https://github.com/moony1992/Concurrent-Homework/blob/master/image.png)


ConcurrentHashMap的原理分析
====

1.ConcurrentHashMap1.8中是基于什么机制来保证线程安全性的？
----

ConcurrentHashMap1.8中的数据结构是基于数组加链表进行数据存储的，线程安全采用的是cas和synchronize关键字锁住数组中的一个元素，来对元素实现写的功能。

2.ConcurrentHashMap通过get方法获取数据的时候，是否需要通过加锁来保证数据的可见性？为什么？
----

不需要。因为get方法内通过tabAt获取value值，内部是getObjectVolatile。对于volatile关键字，写操作一定happens-before读操作。因此其他线程对value的修改，都能被get获取到最新的值，被get读取可见。

3.ConcurrentHashMap1.7和ConcurrentHashMap1.8有哪些区别？
----

相对于1.7来说，1.8版本做了两个改进
一.取消了segment分段设计，而直接采用node数组来保存数据，在保证线程安全时，使锁的粒度更小更细。
二.由1.7数组+链表的方式升级为1.8的数组+链表+红黑树，优化了计算方式，数据量大时使查询效率更快。

4.ConcurrentHashMap1.8为什么要引入红黑树？
----

1.8以后，对链表长度大于8，数组大小扩容超过64位时，会将链表修改为红黑树。对于查找来说，线性查找的复杂度在O(n),当n值增加到一定大小时，会严重影响到查询效率。而相对于红黑树来说，时间复杂度为O(log(n)),数据量越大查找的速度相对于线性链表越来得快
