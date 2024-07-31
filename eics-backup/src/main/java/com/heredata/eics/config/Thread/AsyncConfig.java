package com.heredata.eics.config.Thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * <p>Title: AsyncConfig</p>
 * <p>Description:线程池配置 </p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: SI-TECH </p>
 *
 * @author dingrb
 * @version 1.0
 * @createtime 2020-05-13 14:22
 */

public class AsyncConfig {

        @Value( "${core.pool.size}" )
        private int corePoolSize;

         @Value("${max.pool.size}")
         private int maxPoolSize;

        @Value("${queue.capacity}")
        private int queueCapacity;

        @Value( "${keepAlive.second}" )
        private int keepAliveTime;


            @Bean("async")
            public TaskExecutor taskExecutor() {
                ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
                // 设置核心线程数
                executor.setCorePoolSize(corePoolSize);
                // 设置最大线程数
                executor.setMaxPoolSize(maxPoolSize);
                // 设置队列容量
                executor.setQueueCapacity(queueCapacity);
                // 设置线程活跃时间（秒）
                executor.setKeepAliveSeconds(keepAliveTime);
                // 设置默认线程名称
                executor.setThreadNamePrefix("task-");
                // 设置拒绝策略
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
                // 等待所有任务结束后再关闭线程池
                executor.setWaitForTasksToCompleteOnShutdown(true);
                return executor;
            }

}
