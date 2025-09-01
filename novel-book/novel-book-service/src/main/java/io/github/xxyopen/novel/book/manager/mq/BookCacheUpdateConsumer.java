package io.github.xxyopen.novel.book.manager.mq;

import io.github.xxyopen.novel.book.manager.cache.BookInfoCacheManager;
import io.github.xxyopen.novel.common.constant.AmqpConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 小说信息变更消息消费者
 * 用于更新Redis缓存
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.amqp.enabled", havingValue = "true")
public class BookCacheUpdateConsumer {

    private final BookInfoCacheManager bookInfoCacheManager;

    /**
     * 处理小说信息变更消息，更新Redis缓存
     *
     * @param bookId 小说ID
     */
    @RabbitListener(queues = AmqpConsts.BookChangeMq.QUEUE_REDIS_UPDATE)
    public void handleBookChange(Long bookId) {
        try {
            log.info("收到小说信息变更消息，开始更新Redis缓存，bookId: {}", bookId);
            
            // 清除小说信息缓存，下次查询时会重新加载
            bookInfoCacheManager.evictBookInfoCache(bookId);
            
            log.info("小说Redis缓存更新成功，bookId: {}", bookId);
        } catch (Exception e) {
            log.error("处理小说信息变更消息失败，bookId: {}", bookId, e);
        }
    }
}
