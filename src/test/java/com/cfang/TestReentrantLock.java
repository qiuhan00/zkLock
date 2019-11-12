package com.cfang;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestReentrantLock {

    private Lock lock = new ReentrantLock();
    
    private void m1(){
        try{
            lock.lock();
            for(int i = 0; i < 10; i++){
                TimeUnit.SECONDS.sleep(1);
                System.out.println("m1() method " + i);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }
    
    private void m2(){
        boolean isLocked = false;
        try {
            //尝试锁，如果已被其他线程锁住，无法获取锁标记，则返回false
            //相反，如果获取锁标记，则返回true
            //isLocked = lock.tryLock();
            
            //阻塞尝试锁：会阻塞参数代表的时长，再去尝试获取锁标记
            //如果超时未获取，不继续等待，直接返回false
            //阻塞尝试锁类似于自旋锁。
            isLocked = lock.tryLock(11, TimeUnit.SECONDS);
            if(isLocked){
                System.out.println("method m2 synchronized");
            }else{
                System.out.println("method m2 unsynchronized");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(isLocked){
                lock.unlock();
            }
        }
    }
    
    public static void main(String[] args) {
        TestReentrantLock t = new TestReentrantLock();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                t.m1();
            }
        }).start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                t.m2();
            }
        }).start();
    }
}