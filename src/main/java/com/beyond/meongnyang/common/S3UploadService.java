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
import java.util.regex.PatternSyntaxException;

@Service
@RequiredArgsConstructor
public class S3UploadService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 단일 파일 업로드
    public String upload(MultipartFile file) {
        String fileName = "user-" + UUID.randomUUID() + "-profileimage-" + file.getOriginalFilename();

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
        String imgUrl = s3Client.utilities().getUrl(a -> a.bucket(bucket).key(fileName)).toExternalForm();
        return imgUrl;
    }

    // 다중 파일 업로드
    public List<String> upload(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
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
    public void delete(String fileName) {
        s3Client.deleteObject(a -> a.bucket(bucket).key(fileName));
    }

    /**
     * 주어진 파일 목록을 지정된 패턴에 따라 파일명을 생성해 S3 버킷에 업로드하고, 업로드된 파일의 URL 목록을 반환합니다.<br>
     * 패턴 예시: chat/{roomId}/{messageId}/chat-{roomId}-{messageId}-*  (여기서 *은 파일 순번으로 치환됩니다.)
     *
     * <p>
     * 각 파일에 대해 다음과 같이 처리합니다.
     * <ul>
     *   <li>파일명에서 확장자를 추출합니다. 확장자가 없으면 예외가 발생합니다.</li>
     *   <li>패턴 문자열 내에 반드시 *가 포함되어 있어야 하며, * 자리에 파일의 순번(i, 0부터 시작)을 삽입해 최종 key를 생성합니다.</li>
     *   <li>생성된 key와 함께 파일을 지정된 S3 버킷에 업로드합니다. 업로드 실패 시 예외를 발생시킵니다.</li>
     *   <li>업로드가 성공적으로 완료되면 해당 객체의 S3 URL을 목록에 추가합니다.</li>
     * </ul>
     * </p>
     *
     * @param files   업로드할 MultipartFile 객체 리스트입니다.
     * @param pattern 파일 저장 경로 및 이름 지정 패턴입니다. (예: "chat/{roomId}/{messageId}/chat-{roomId}-{messageId}-*")
     * @return 업로드된 파일의 S3 URL 문자열 리스트를 반환합니다.
     * @throws IllegalArgumentException 파일명에 확장자가 없거나, 패턴 양식에 *이 없거나, 업로드 실패 시 발생합니다.
     * @throws PatternSyntaxException   패턴 양식이 올바르지 않을 때 발생합니다.
     */
    public List<String> upload(List<MultipartFile> files, String pattern) {

        if ((pattern).indexOf('*') == -1)
            throw new PatternSyntaxException("패턴 양식이 올바르지 않습니다.", pattern, -1);

        List<String> urls = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String[] splitFileName = null;

            try {
                splitFileName = file.getOriginalFilename().split("\\.");
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("처리할 수 없는 파일명입니다. 확장자명이 반드시 필요합니다.");
            }

            String extension = splitFileName[splitFileName.length - 1];

            String key = (pattern + extension).replace("*", String.valueOf(i));

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
