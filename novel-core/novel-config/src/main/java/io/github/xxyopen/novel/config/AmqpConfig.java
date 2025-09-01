package io.github.xxyopen.novel.config;

import io.github.xxyopen.novel.common.constant.AmqpConsts;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AMQP 配置类
 */
@Configuration
public class AmqpConfig {

    /**
     * 小说信息改变交换机
     */
    @Bean
    public FanoutExchange bookChangeExchange() {
        return new FanoutExchange(AmqpConsts.BookChangeMq.EXCHANGE_NAME);
    }

    /**
     * Elasticsearch book 索引更新队列
     */
    @Bean
    public Queue esBookUpdateQueue() {
        return new Queue(AmqpConsts.BookChangeMq.QUEUE_ES_UPDATE);
    }

    /**
     * Redis book 缓存更新队列
     */
    @Bean
    public Queue redisBookUpdateQueue() {
        return new Queue(AmqpConsts.BookChangeMq.QUEUE_REDIS_UPDATE);
    }

    /**
     * Elasticsearch book 索引更新队列绑定到小说信息改变交换机
     */
    @Bean
    public Binding esBookUpdateQueueBinding() {
        return BindingBuilder.bind(esBookUpdateQueue()).to(bookChangeExchange());
    }

    /**
     * Redis book 缓存更新队列绑定到小说信息改变交换机
     */
    @Bean
    public Binding redisBookUpdateQueueBinding() {
        return BindingBuilder.bind(redisBookUpdateQueue()).to(bookChangeExchange());
    }

}
