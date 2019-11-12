package com.cfang.zkClient;

import java.io.UnsupportedEncodingException;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

public class MyZkSerializer implements ZkSerializer {

	@Override
	public byte[] serialize(Object data) throws ZkMarshallingError {
		try {
			return String.valueOf(data).getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new ZkMarshallingError(e);
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws ZkMarshallingError {
		try {
			return new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new ZkMarshallingError(e);
		}
	}

}
