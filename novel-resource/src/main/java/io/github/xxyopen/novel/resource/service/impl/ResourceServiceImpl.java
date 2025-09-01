package io.github.xxyopen.novel.resource.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.github.xxyopen.novel.common.constant.ErrorCodeEnum;
import io.github.xxyopen.novel.common.constant.SystemConfigConsts;
import io.github.xxyopen.novel.common.resp.RestResp;
import io.github.xxyopen.novel.config.exception.BusinessException;
import io.github.xxyopen.novel.resource.dto.resp.ImgVerifyCodeRespDto;
import io.github.xxyopen.novel.resource.manager.redis.VerifyCodeManager;
import io.github.xxyopen.novel.resource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 资源（图片/视频/文档）相关服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final VerifyCodeManager verifyCodeManager;

    @Value("${novel.file.upload.path}")
    private String fileUploadPath;

    @Override
    public RestResp<ImgVerifyCodeRespDto> getImgVerifyCode() throws IOException {
        String sessionId = IdWorker.get32UUID();
        return RestResp.ok(ImgVerifyCodeRespDto.builder()
            .sessionId(sessionId)
            .img(verifyCodeManager.genImgVerifyCode(sessionId))
            .build());
    }

    @SneakyThrows
    @Override
    public RestResp<String> uploadImage(MultipartFile file) {
        LocalDateTime now = LocalDateTime.now();
        String savePath =
                SystemConfigConsts.IMAGE_UPLOAD_DIRECTORY
                        + now.format(DateTimeFormatter.ofPattern("yyyy")) + File.separator
                        + now.format(DateTimeFormatter.ofPattern("MM")) + File.separator
                        + now.format(DateTimeFormatter.ofPattern("dd"));
        String oriName = file.getOriginalFilename();
        assert oriName != null;
        String saveFileName = IdWorker.get32UUID() + oriName.substring(oriName.lastIndexOf("."));
        File saveFile = new File(fileUploadPath + savePath, saveFileName);

        // 确保父目录存在
        File parentDir = saveFile.getParentFile();
        if (!parentDir.exists()) {
            // 使用 mkdirs() 创建所有必要的父目录
            if (!parentDir.mkdirs()) {
                log.error("无法创建目录: {}", parentDir.getAbsolutePath());
                throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_ERROR);
            }
        } else if (!parentDir.isDirectory()) {
            log.error("路径已存在但不是目录: {}", parentDir.getAbsolutePath());
            throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_ERROR);
        } else if (!parentDir.canWrite()) {
            log.error("目录没有写入权限: {}", parentDir.getAbsolutePath());
            throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_ERROR);
        }

        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            log.error("文件保存失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_ERROR);
        }

        // 检查文件是否为有效图片
        try {
            if (Objects.isNull(ImageIO.read(saveFile))) {
                // 上传的文件不是图片
                Files.delete(saveFile.toPath());
                throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_TYPE_NOT_MATCH);
            }
        } catch (IOException e) {
            // 如果读取文件出错，也删除文件并抛出异常
            try {
                Files.deleteIfExists(saveFile.toPath());
            } catch (IOException deleteEx) {
                log.warn("删除无效文件失败: {}", saveFile.getAbsolutePath(), deleteEx);
            }
            log.error("文件读取失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_ERROR);
        }

        return RestResp.ok(savePath + File.separator + saveFileName);
    }

}
