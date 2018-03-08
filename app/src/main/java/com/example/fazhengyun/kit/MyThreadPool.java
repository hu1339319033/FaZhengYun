package com.example.fazhengyun.kit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadPool {
	private static MyThreadPool myThreadPool=new MyThreadPool();
	private ExecutorService executorService;
	/**
	 * ˽�л����췽��
	 */
	private MyThreadPool(){
		 executorService=Executors.newSingleThreadExecutor();
		
	}
	
	public static MyThreadPool getInstance(){
		return myThreadPool;
	}
	
	public ExecutorService getMyExecutorService(){
		return executorService;
	}
	
}
