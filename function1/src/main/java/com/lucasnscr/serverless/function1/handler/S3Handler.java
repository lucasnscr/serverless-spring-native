package com.lucasnscr.serverless.function1.handler;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class S3Handler implements RequestHandler<S3Event, String> {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.accesskey}")
    private String awsAccessKeyId;

    @Value("${aws.secretkey}")
    private String awsSecretAccessKey;

    @Value("${aws.s3.endpoint}")
    private String awsEndpoint;

    @Value("${application.bucket.name}")
    private String bucketName;

    private final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);

    AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion))
            .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
            .build();

    @Override
    public String handleRequest(S3Event s3Event, Context context) {

        boolean checkAccessToBucket = checkAccessToBucket(bucketName);
        if (!checkAccessToBucket){
            s3Client.createBucket(bucketName);
        }

        final String bucketName = s3Event.getRecords().get(0).getS3().getBucket().getName();
        final String fileName = s3Event.getRecords().get(0).getS3().getObject().getKey();

        log.info("File - "+ fileName+" uploaded into "+
                bucketName+" bucket at "+ s3Event.getRecords().get(0).getEventTime());

        try{
            InputStream objectContent = s3Client.getObject(bucketName, fileName).getObjectContent();
            log.info("File Contents : " + StreamUtils.copyToString(objectContent, StandardCharsets.UTF_8));
        }catch (Exception e){
            log.error("Error in upload file to S3:" + e.getMessage());
            return "Error reading contents of the file";
        }
        return null;
    }

    private boolean checkAccessToBucket(final String bucketName) {
        try {
            final HeadBucketRequest request = new HeadBucketRequest(bucketName);
            s3Client.headBucket(request);
            return true;
        } catch (AmazonServiceException ex) {
            if (ex.getStatusCode() == 404 | ex.getStatusCode() == 403 || ex.getStatusCode() == 301) {
                return false;
            }
            throw ex;
        } catch (Exception ex) {
            log.error("Cannot check access", ex);
            throw ex;
        }
    }


}
