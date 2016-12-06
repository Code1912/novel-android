package com.code1912.novelapp.utils;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Code1912 on 2016/12/6.
 */

public class Transporter {

	private Lock lock = new ReentrantLock();// 锁对象
      	HashMap<String, Object>  transportData = new HashMap<String,  Object>();
	public  final  static Transporter  instance=new Transporter();
	public  <T> T getTransportData(String transportId) {
		lock.lock();
		T data = null;
		try {
			if (transportData.containsKey(transportId)) {
				data = (T) transportData.get(transportId);
				transportData.remove(transportId);
			}

		} finally {
			lock.unlock();// 释放锁
		}
		return data;
	}

	public <T>String putObject(T obj){
	   return  innerPutData( Util.deepClone(obj));
	}
	public <T>String putArray(List<T> obj){
		return  innerPutData( Util.deepCloneArray(obj));
	}
	private   String innerPutData(Object data) {
		lock.lock();
		String id = UUID.randomUUID().toString();
		try {
			transportData.put(id, data);
		} finally {
			lock.unlock();// 释放锁
		}
		return id;
	}

}
