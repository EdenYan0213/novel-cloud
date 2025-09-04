package io.github.xxyopen.novel.user.manager.cache;

import io.github.xxyopen.novel.common.constant.CacheConsts;
import io.github.xxyopen.novel.user.dao.entity.UserInfo;
import io.github.xxyopen.novel.user.dao.mapper.UserInfoMapper;
import io.github.xxyopen.novel.user.dto.UserInfoDto;
import io.github.xxyopen.novel.user.dto.req.UserInfoUptReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户信息 缓存管理类
 */
@Component
@RequiredArgsConstructor
public class UserInfoCacheManager {

    private final UserInfoMapper userInfoMapper;

    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
            value = CacheConsts.USER_INFO_CACHE_NAME)
    public UserInfoDto getUser(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (Objects.isNull(userInfo)) {
            return null;
        }
        return UserInfoDto.builder()
                .id(userInfo.getId())
                .status(userInfo.getStatus()).build();
    }

    /**
     * 更新用户信息时主动更新缓存
     */
    @CachePut(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
            value = CacheConsts.USER_INFO_CACHE_NAME,
            key = "#dto.userId")
    public UserInfoDto updateUser(UserInfoUptReqDto dto) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(dto.getUserId());
        userInfo.setNickName(dto.getNickName());
        userInfo.setUserPhoto(dto.getUserPhoto());
        userInfo.setUserSex(dto.getUserSex());
        userInfoMapper.updateById(userInfo);

        // 返回更新后的用户信息用于缓存
        UserInfo updatedUser = userInfoMapper.selectById(dto.getUserId());
        return convertToDto(updatedUser);
    }

    /**
     * 删除用户缓存
     */
    @CacheEvict(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
            value = CacheConsts.USER_INFO_CACHE_NAME,
            key = "#userId")
    public void evictUserCache(Long userId) {
        // 仅清除缓存，不执行其他操作
    }

    private UserInfoDto convertToDto(UserInfo userInfo) {
        return UserInfoDto.builder()
                .id(userInfo.getId())
                .status(userInfo.getStatus())
                .build();
    }
}
