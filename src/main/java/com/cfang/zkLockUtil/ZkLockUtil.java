package com.cfang.zkLockUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang3.StringUtils;

import com.cfang.zkClient.MyZkSerializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZkLockUtil implements Lock{
	
	private String znode;
	private ZkClient zkClient;
	
	public ZkLockUtil(String znode) {
		if(StringUtils.isBlank(znode)) {
			throw new IllegalArgumentException("锁节点znode不能为空字符串");
		}
		this.znode = znode;
		this.zkClient = new ZkClient("111.231.51.200:2181,111.231.51.200:2182,111.231.51.200:2183");
		this.zkClient.setZkSerializer(new MyZkSerializer());
	}

	@Override
	public void lock() {
		if(!tryLock()) { //抢锁失败
			// 阻塞等待锁节点的释放
			waitLock();
			//递归调用，重新尝试去抢占锁
			lock();
		}
	}
	
	private void waitLock() {
		CountDownLatch latch = new CountDownLatch(1);
		// 注册监听znode锁节点变化，当删除的时候，说明锁被释放
		IZkDataListener listener = new IZkDataListener() {
			
			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				log.info("znode节点被删除，锁释放...");
				latch.countDown();
			}
			
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
			}
		};
		this.zkClient.subscribeDataChanges(this.znode, listener);
		try {
			// 阻塞等待锁znode节点的删除释放
			if(this.zkClient.exists(znode)) {
				latch.await();
			}
		} catch (Exception e) {
		}
		//取消znode节点监听
		this.zkClient.unsubscribeDataChanges(this.znode, listener);
	}
	
	@Override
	public boolean tryLock() {
		boolean result = false;
		try {
			this.zkClient.createEphemeral(znode); //创建临时节点
			result = true;
		} catch (ZkNodeExistsException e) {
			log.warn("锁节点znode已存在，抢占失败...");
			result = false;
		} catch (Exception e) {
			log.warn("创建锁节点znode异常,{}...", e.getMessage());
		}
		return result;
	}

	@Override
	public void unlock() {
		zkClient.delete(znode);
	}
	
	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
