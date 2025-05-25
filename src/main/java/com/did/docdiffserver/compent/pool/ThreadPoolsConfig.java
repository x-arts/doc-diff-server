package com.did.docdiffserver.compent.pool;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
public class ThreadPoolsConfig {

	@Bean(name = "threadPoolTaskExecutor")
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(10);
		threadPoolTaskExecutor.setMaxPoolSize(1000);
		threadPoolTaskExecutor.setQueueCapacity(10000);
		threadPoolTaskExecutor.setThreadNamePrefix("Diff-task-");
		threadPoolTaskExecutor.setKeepAliveSeconds(60);
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}
	
	@Primary
	@Bean(name = "listeningExecutorService")
	public ListeningExecutorService listeningExecutorService(@Qualifier("threadPoolTaskExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
		ListeningExecutorService executorService = MoreExecutors
				.listeningDecorator(threadPoolTaskExecutor.getThreadPoolExecutor());
		return executorService;
	}










}