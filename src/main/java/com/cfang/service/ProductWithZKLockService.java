package com.cfang.service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cfang.dao.ProductDao;
import com.cfang.zkLockUtil.ZKLockImproveUtil;
import com.cfang.zkLockUtil.ZkLockUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Scope("prototype")
public class ProductWithZKLockService {
	
//	private Lock lock = new ZkLockUtil("/p1node");
	private Lock lock = new ZKLockImproveUtil("/pnode");

	@Autowired
	private ProductDao productDao;
	
	@Transactional
	public boolean buy(String userName, String productname, int number) {
		boolean result = false;
		lock.lock();
		try {
			log.info("用户{}欲购买{}个{}",  userName, number, productname);
			int stock = productDao.getStock(productname);
			if(stock < number) {
				log.warn("库存不足...");
				return false;
			}
			result = productDao.buy(userName, productname, number);
		} catch (Exception e) {
			
		} finally {
			lock.unlock();
		}
		log.info("{}购买结果，{}",userName,  result);
		return result;
	}
}
