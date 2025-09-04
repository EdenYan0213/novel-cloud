package io.github.xxyopen.novel.user.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.xxyopen.novel.user.dao.entity.UserInfo;
import io.github.xxyopen.novel.user.dao.mapper.UserInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmUpService {

    private final UserInfoMapper userInfoMapper;
    private final UserInfoCacheManager userInfoCacheManager;

    @EventListener(ApplicationReadyEvent.class)
    public void warmUpUserCache() {
        log.info("开始预热用户信息缓存...");

        try {
            // 方式1: 预热活跃用户
            preheatActiveUsers();

            // 方式2: 预热VIP用户
            preheatVipUsers();

            log.info("用户信息缓存预热完成");
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        }
    }

    /**
     * 预热活跃用户（最近登录的用户）
     */
    private void preheatActiveUsers() {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time")
                .last("LIMIT 1000"); // 预热最近1000个活跃用户

        List<UserInfo> activeUsers = userInfoMapper.selectList(queryWrapper);
        activeUsers.forEach(user -> {
            // 触发缓存加载
            userInfoCacheManager.getUser(user.getId());
        });

        log.info("预热活跃用户数量: {}", activeUsers.size());
    }

    /**
     * 预热VIP用户（可以根据业务需求定义）
     */
    private void preheatVipUsers() {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("account_balance", 10000) // 余额大于10000的用户
                .last("LIMIT 500");

        List<UserInfo> vipUsers = userInfoMapper.selectList(queryWrapper);
        vipUsers.forEach(user -> {
            // 触发缓存加载
            userInfoCacheManager.getUser(user.getId());
        });

        log.info("预热VIP用户数量: {}", vipUsers.size());
    }
}
