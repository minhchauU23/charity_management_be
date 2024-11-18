package dev.ptit.charitymanagement.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;


@Configuration
public class S3Config {
    @Value("${aws.access-key}")
    private String awsAccessKey;

    @Value("${aws.secret-key}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

//    private final EnvironmentVariableCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();

    @Bean
    S3Client s3Client(){
        Region region = Region.of(awsRegion);
        AwsBasicCredentials awsCredential = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        return S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredential))
                .build();
    }

    @Bean
    S3Presigner s3Presigner(){
        Region region = Region.of(awsRegion);
        AwsBasicCredentials awsCredential = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);

        return S3Presigner.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredential))
                .build();
    }

}
