package com.cfang.service;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.cfang.dao.ProductDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Scope("prototype")
public class ProductWithLockService {
	
	private Lock lock = new ReentrantLock();

	@Autowired
	private ProductDao productDao;
	
	@Transactional
	public boolean buy(String userName, String productname, int number) {
		boolean result = false;
		try {
			lock.lock();
//			TimeUnit.SECONDS.sleep(1);
			log.info("用户{}欲购买{}个{}",  userName, number, productname);
			int stock = productDao.getStock(productname);
			log.info("{} 查询数量{}...", userName, stock);
			if(stock < number) {
				log.warn("库存不足...");
				return false;
			}
			result = productDao.buy(userName, productname, number);
		} catch (Exception e) {
			
		} finally {
			log.info("{} 释放锁...", userName);
			lock.unlock();
		}
		log.info("{}购买结果，{}",userName,  result);
		return result;
	}
}
