package com.cfang.zkLockUtil;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;

import com.cfang.zkClient.MyZkSerializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZKLockImproveUtil implements Lock{
	
	private String znode;
	private ZkClient zkClient;
	private ThreadLocal<String> currentNode = new ThreadLocal<String>(); //当前节点
	private ThreadLocal<String> beforeNode = new ThreadLocal<String>();  //前一个节点
	
	public ZKLockImproveUtil(String znode) {
		if(StringUtils.isBlank(znode)) {
			throw new IllegalArgumentException("锁节点znode不能为空字符串");
		}
		this.znode = znode;
		this.zkClient = new ZkClient("111.231.51.200:2181,111.231.51.200:2182,111.231.51.200:2183");
		this.zkClient.setZkSerializer(new MyZkSerializer());
		
		try {
			if(!this.zkClient.exists(znode)) {
				this.zkClient.createPersistent(znode, true); // true是否创建层级目录
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void lock() {
		if(!tryLock()) {
			waitLock();
			lock();
		}
	}
	
	private void waitLock() {
		CountDownLatch latch = new CountDownLatch(1);
		IZkDataListener listener = new IZkDataListener() {
			
			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				log.info("{}节点删除，锁释放...", dataPath);
				latch.countDown();
			}
			
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
			}
		};
		
		this.zkClient.subscribeDataChanges(beforeNode.get(), listener);
		
		try {
			if(this.zkClient.exists(beforeNode.get())) {
				latch.await();
			}
		} catch (Exception e) {
		}
		
		this.zkClient.unsubscribeDataChanges(beforeNode.get(), listener);
	}

	@Override
	public boolean tryLock() {
		boolean result = false;
		// 创建顺序临时节点
		if(null == currentNode.get() || !this.zkClient.exists(currentNode.get())) {
			String enode = this.zkClient.createEphemeralSequential(znode + "/", "zk-locked");
			this.currentNode.set(enode);
		}
		// 获取znode节点下的所有子节点
		List<String> list = this.zkClient.getChildren(znode);
		Collections.sort(list);
		
		/**
		 * 如果当前节点是第一个的话，则是为获取锁，继续执行
		 * 不是头结点的话，则去查询其前面一个节点，然后准备监听前一个节点的删除释放操作
		 */
		
		if(currentNode.get().equals(this.znode + "/" + list.get(0))) {
			log.info("{}节点为头结点，获得锁...", currentNode.get());
			result = true;
		} else {
			int currentIndex = list.indexOf(currentNode.get().substring(this.znode.length() + 1));
			String bnode = this.znode + "/" + list.get(currentIndex - 1);
			this.beforeNode.set(bnode);
		}
		return result;
	}

	@Override
	public void unlock() {
		if(null != this.currentNode) {
			this.zkClient.delete(currentNode.get());
			this.currentNode.set(null);
		}
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
