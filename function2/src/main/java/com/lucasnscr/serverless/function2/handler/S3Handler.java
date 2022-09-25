package com.lucasnscr.serverless.function2.handler;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class S3Handler implements RequestHandler<S3Event, ByteArrayOutputStream> {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.accesskey}")
    private String awsAccessKeyId;

    @Value("${aws.secretkey}")
    private String awsSecretAccessKey;

    @Value("${aws.s3.endpoint}")
    private String awsEndpoint;

    @Value("${bucket.name}")
    private String bucketName;

    private final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);

    AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion))
            .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
            .build();

    @Override
    public ByteArrayOutputStream handleRequest(S3Event s3Event, Context context) {

        S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);
        String keyName = record.getS3().getObject().getKey();

        try {
                S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, keyName));

                InputStream is = s3object.getObjectContent();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int len;
                byte[] buffer = new byte[4096];
                while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                return outputStream;
            } catch (IOException ioException) {
                log.error("IOException: " + ioException.getMessage());
            } catch (AmazonServiceException serviceException) {
                log.info("AmazonServiceException Message:    " + serviceException.getMessage());
                throw serviceException;
            } catch (AmazonClientException clientException) {
                log.info("AmazonClientException Message: " + clientException.getMessage());
                throw clientException;
            }
            return null;
        }
}
