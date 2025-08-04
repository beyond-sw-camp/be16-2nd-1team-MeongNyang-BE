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

    // 단일 파일 업로드
    public String upload(MultipartFile file){
        String fileName = "user-"+UUID.randomUUID()+"-profileimage-"+file.getOriginalFilename();

        // 저장 객체 구성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(file.getContentType()) // image//jpg
                .build();

        // 이미지를 업로드 ( byte 형태로 )
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            // checked -> unchecked로 바꿔 전체 rollback 되도록 예외처리
            throw new IllegalArgumentException("이미지 업로드 실패");
        }

        //이미지 url 추출
        String imgUrl = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();
        return imgUrl;
    }

    // 다중 파일 업로드
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

    // 파일 삭제
    public void delete(String fileName){
        s3Client.deleteObject(a->a.bucket(bucket).key(fileName));
    }
}
