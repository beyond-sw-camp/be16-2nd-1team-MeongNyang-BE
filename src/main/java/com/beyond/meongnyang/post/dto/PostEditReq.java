package com.beyond.meongnyang.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostEditReq {
    private String title;
    private String content;
    private List<String> existingMediaUrls; // 기존 미디어 URL들
    private List<MultipartFile> fileList;
}
