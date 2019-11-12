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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cfang.dao.ProductDao;
import com.cfang.service.ProductService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ProductTest {
	
	long timed = 0L;
	
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductService productService;

    @Before
    public void start() {
        System.out.println("DB Lock开始测试");
    }

    @After
    public void end() {
        System.out.println("DB Lock结束测试,执行时长：" + (System.currentTimeMillis() - timed));
    }
    
    @Test
    public void testDao() {
    	int result = productDao.getStock("feizao");
    	
    	System.out.println(result);
    }
    
    @Test
    public void benchmark() throws InterruptedException {
    	// 模拟的请求数量
        final int threadNum = 10;
        CountDownLatch cdl = new CountDownLatch(threadNum);
        List<Thread> threads = new ArrayList<Thread>();
        String productName = "feizao";
        int buyNum = 10;
        
        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Thread(() -> {
        		try {
        			// 等待cdl值为0，也就是其他线程就绪后，再运行后续的代码
        			cdl.await();
        			// http请求实际上就是多线程调用这个方法
        			productService.buy(Thread.currentThread().getName(), productName, buyNum);
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
        	});
            threads.add(thread);
            thread.start();
            cdl.countDown();
        }

        // 等待上面所有线程执行完毕之后，结束测试
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
