package dev.ptit.charitymanagement.service.image;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    @Value("${aws.bucket}")
    private String bucketName;
    @Value("${aws.region}")
    private String region;

    private final S3Client s3Client;
    private  final  S3Presigner s3Presigner;


    public String generatePreSignedUrl(String fileName) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))// The URL expires in 10 minutes.
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String preSignedUrl = presignedRequest.url().toString();
        log.info("Presigned URL to upload a file to: [{}]", preSignedUrl);
        log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());
        return presignedRequest.url().toExternalForm();
    }

    public String extractFileExtension(String rawName){
        if (rawName == null || rawName.isEmpty()) {
            return "";
        }
        int dotIndex = rawName.lastIndexOf('.');
        if (dotIndex == -1) {
            return ""; // No extension
        }
        return rawName.substring(dotIndex);
    }

    public String generateFileLink(String fileName){
        return "https://" + bucketName+ ".s3." + region+ ".amazonaws.com/" + fileName;
    }



}
