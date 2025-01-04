package dev.ptit.charitymanagement.service.image;


import dev.ptit.charitymanagement.dtos.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public String generateFileLink(String fileName){
        return "https://" + bucketName+ ".s3." + region+ ".amazonaws.com/" + fileName;
    }

    public List<String> generatePreSignedUrl(List<String> fileNames){
        List<String> preSignedUrls = new ArrayList<>();
        for(String fileName : fileNames){
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))// The URL expires in 10 minutes.
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest preSignedRequest = s3Presigner.presignPutObject(preSignRequest);
            String preSignedUrl = preSignedRequest.url().toString();
            log.info("Presigned URL to upload a file to: [{}]", preSignedUrl);
            log.info("HTTP method: [{}]", preSignedRequest.httpRequest().method());
            preSignedUrls.add(preSignedRequest.url().toExternalForm());
        }
        return preSignedUrls;
    }

//    public Image preSignedUpload(Image image) {
//        String folder = "temp" + "-" + UUID.randomUUID().toString();
//
//        PutObjectRequest objectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(folder+"/"+image.getFileName())
//                .build();
//
//        PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(10))// The URL expires in 10 minutes.
//                .putObjectRequest(objectRequest)
//                .build();
//
//        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(preSignRequest);
//
//        String preSignedUrl = presignedPutObjectRequest.url().toString();
//        log.info("Presigned URL to upload a file to: [{}]", preSignedUrl);
//        log.info("HTTP method: [{}]", presignedPutObjectRequest.httpRequest().method());
//        return Image.builder()
//                .id(folder+"/"+image.getFileName())
//                .fileName(image.getFileName())
//                .folder(folder)
//                .preSignedUrl(presignedPutObjectRequest.url().toExternalForm())
//                .url(generateFileLink(folder+"/"+image.getFileName()))
//                .build();
//    }
//
//    public void moveFile(Image sourceImage, Image destinationImage){
//        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
//                .sourceBucket(bucketName)
//                .sourceKey(sourceImage.getFolder() +"/" + sourceImage.getFileName())
//                .destinationBucket(bucketName)
//                .destinationKey(sourceImage.getFolder() + "/" + sourceImage.getFileName())
//                .build();
//
//        s3Client.copyObject(copyRequest);
//        deleteFile(sourceImage);
//    }
//
//    public void deleteFile(Image image){
//        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
//                .bucket(bucketName)
//                .key(image.getFolder() + "/" + image.getFileName())
//                .build();
//        s3Client.deleteObject(deleteRequest);
//    }

}
