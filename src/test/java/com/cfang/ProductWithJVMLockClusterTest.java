package com.cfang;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cfang.service.ProductWithLockService;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class ProductWithJVMLockClusterTest {
	
	long timed = 0L;
	
	@Autowired
	private ApplicationContext context;

    @Before
    public void start() {
    	timed = System.currentTimeMillis();
        log.info("Cluster JVM Lock开始测试");
    }

    @After
    public void end() {
    	log.info("Cluster JVM Lock结束测试,执行时长:{} ms", (System.currentTimeMillis() - timed));
    }
    
    @Test
    public void buy() throws InterruptedException {
    	// 模拟的请求数量
        final int threadNum = 10;
        CountDownLatch cdl = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<Thread>();
        String productName = "feizao";
        int buyNum = 10;
        
        for (int i = 0; i < threadNum; i++) {
        	ProductWithLockService productWithLockService = context.getBean(ProductWithLockService.class);
            Thread thread = new Thread(() -> {
        		try {
        			cdl.await();
        			// http请求实际上就是多线程调用这个方法
        			productWithLockService.buy(Thread.currentThread().getName(), productName, buyNum);
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
        	});
            threads.add(thread);
            thread.start();
        }
        cdl.countDown();

        // 等待上面所有线程执行完毕之后，结束测试
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
