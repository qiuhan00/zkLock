package com.cfang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cfang.dao.ProductDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {

	@Autowired
	private ProductDao productDao;
	
	@Transactional
	public boolean buy(String userName, String productname, int number) {
		log.info("用户{}欲购买{}个{}",  userName, number, productname);
		boolean result = false;
		try {
			result = productDao.buy(userName, productname, number);
			log.info("{}购买结果:{}",userName,  result);
		} catch (Exception e) {
			log.error("buy err,{}...", e.getMessage());
		}
		return result;
	}
}
