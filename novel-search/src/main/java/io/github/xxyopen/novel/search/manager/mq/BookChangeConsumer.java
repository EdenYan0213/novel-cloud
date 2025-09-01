package io.github.xxyopen.novel.search.manager.mq;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import io.github.xxyopen.novel.book.dto.resp.BookEsRespDto;
import io.github.xxyopen.novel.book.dto.resp.BookInfoRespDto;
import io.github.xxyopen.novel.book.feign.BookFeign;
import io.github.xxyopen.novel.common.constant.AmqpConsts;
import io.github.xxyopen.novel.common.resp.RestResp;
import io.github.xxyopen.novel.search.constant.EsConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 小说信息变更消息消费者
 * 用于更新Elasticsearch索引
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.amqp.enabled", havingValue = "true")
public class BookChangeConsumer {

    private final BookFeign bookFeign;
    private final ElasticsearchClient elasticsearchClient;

    /**
     * 处理小说信息变更消息，更新ES索引
     *
     * @param bookId 小说ID
     */
    @RabbitListener(queues = AmqpConsts.BookChangeMq.QUEUE_ES_UPDATE)
    public void handleBookChange(Long bookId) {
        try {
            log.info("收到小说信息变更消息，开始更新ES索引，bookId: {}", bookId);
            
            // 通过Feign调用获取小说信息
            RestResp<List<BookInfoRespDto>> resp = bookFeign.listBookInfoByIds(List.of(bookId));
            
            if (Objects.equals("00000", resp.getCode()) && resp.getData() != null && !resp.getData().isEmpty()) {
                BookInfoRespDto bookInfo = resp.getData().get(0);
                
                // 转换为BookEsRespDto
                BookEsRespDto bookEsDto = convertToBookEsRespDto(bookInfo);
                
                // 更新ES索引
                IndexRequest<BookEsRespDto> request = IndexRequest.of(i -> i
                    .index(EsConsts.BookIndex.INDEX_NAME)
                    .id(bookEsDto.getId().toString())
                    .document(bookEsDto)
                );
                
                elasticsearchClient.index(request);
                log.info("小说ES索引更新成功，bookId: {}, bookName: {}", bookId, bookInfo.getBookName());
            } else {
                log.warn("获取小说信息失败，bookId: {}, 响应: {}", bookId, resp);
            }
        } catch (Exception e) {
            log.error("处理小说信息变更消息失败，bookId: {}", bookId, e);
        }
    }

    /**
     * 将BookInfoRespDto转换为BookEsRespDto
     */
    private BookEsRespDto convertToBookEsRespDto(BookInfoRespDto bookInfo) {
        return BookEsRespDto.builder()
            .id(bookInfo.getId())
            .bookName(bookInfo.getBookName())
            .categoryId(bookInfo.getCategoryId())
            .categoryName(bookInfo.getCategoryName())
            .authorId(bookInfo.getAuthorId())
            .authorName(bookInfo.getAuthorName())
            .wordCount(bookInfo.getWordCount())
            .lastChapterName(bookInfo.getLastChapterName())
            .build();
    }
}
