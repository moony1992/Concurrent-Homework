
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
