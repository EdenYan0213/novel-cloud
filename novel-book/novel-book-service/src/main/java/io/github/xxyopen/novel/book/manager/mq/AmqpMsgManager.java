package io.github.xxyopen.novel.book.manager.mq;

import io.github.xxyopen.novel.common.constant.AmqpConsts;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * AMQP 消息管理类
 */
@Component
@RequiredArgsConstructor
public class AmqpMsgManager {

    private final AmqpTemplate amqpTemplate;

    @Value("${spring.amqp.enabled:false}")
    private boolean amqpEnabled;

    /**
     * 发送小说信息改变消息
     * 由于使用了Fanout交换机，消息会同时发送到ES更新队列和Redis更新队列
     */
    public void sendBookChangeMsg(Long bookId) {
        if (amqpEnabled) {
            sendAmqpMessage(amqpTemplate, AmqpConsts.BookChangeMq.EXCHANGE_NAME, null, bookId);
        }
    }

    private void sendAmqpMessage(AmqpTemplate amqpTemplate, String exchange, String routingKey,
        Object message) {
        // 如果在事务中则在事务执行完成后再发送，否则可以直接发送
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        amqpTemplate.convertAndSend(exchange, routingKey, message);
                    }
                });
            return;
        }
        amqpTemplate.convertAndSend(exchange, routingKey, message);
    }

}
