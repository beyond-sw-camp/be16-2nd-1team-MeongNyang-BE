package com.beyond.meongnyang.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    public List<String> upload(List<MultipartFile> files){
        List<String> urls = new ArrayList<>();
        for(MultipartFile file : files){
            String key = UUID.randomUUID() + "-" + file.getOriginalFilename();

            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            try {
                s3Client.putObject(putReq, RequestBody.fromBytes(file.getBytes()));
            } catch (Exception e) {
                throw new IllegalArgumentException("이미지 업로드 실패: " + file.getOriginalFilename());
            }

            String imgUrl = s3Client.utilities().getUrl(b -> b.bucket(bucket).key(key)).toExternalForm();
            urls.add(imgUrl);
        }
        return urls;
    }
}
