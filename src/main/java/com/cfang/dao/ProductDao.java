package com.cfang.dao;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cfang.exception.MessageException;

@Repository
public class ProductDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public int getStock(String productname) {
		String sql = "select productnumber from tbl_product where productname = '"+productname+"'";
		int num = jdbcTemplate.queryForObject(sql, Integer.class);
		return num;
	}
	
	public boolean buy(String userName, String productname, int number) {
		String sql = "update tbl_product "
				+ "set productnumber = productnumber - "+number+
				" where productname = '" + productname + "' and productnumber - "+number+" >= 0";
		int count = jdbcTemplate.update(sql);
		if (count != 1) {
			throw new MessageException("商品扣减失败");
		}
		
		// 模拟数据库压力大的场景
//		try {
//			TimeUnit.SECONDS.sleep(5);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		String insertSql = "INSERT INTO tbl_record(productname, username, productnumber, createtime) "
				+ "VALUES('" + productname + "', '" + userName + "', '"+number+"' , now())";
		int insertCount = jdbcTemplate.update(insertSql);
		if (insertCount != 1) {
			throw new MessageException("扣减记录失败");
		}
		return true;
	}
}
