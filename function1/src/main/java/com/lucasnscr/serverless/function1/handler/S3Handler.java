package com.lucasnscr.serverless.function1.handler;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


public class S3Handler implements RequestHandler<S3Event, String> {
    private final String accessKeyId = System.getenv("accessKeyId");
    private final String secretAccessKey = System.getenv("secretAccessKey");
    private final String region = System.getenv("region");
    private final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
    AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
            .build();

    static final Logger log = (Logger) LoggerFactory.getLogger(S3Handler.class);

    @Override
    public String handleRequest(S3Event s3Event, Context context) {

        log.info("");

        final String bucketName = s3Event.getRecords().get(0).getS3().getBucket().getName();
        final String fileName = s3Event.getRecords().get(0).getS3().getObject().getKey();

        log.info("File - "+ fileName+" uploaded into "+
                bucketName+" bucket at "+ s3Event.getRecords().get(0).getEventTime());

        try{
            InputStream objectContent = s3Client.getObject(bucketName, fileName).getObjectContent();
            log.info("File Contents : " + StreamUtils.copyToString(objectContent, StandardCharsets.UTF_8));
        }catch (Exception e){
            log.warning("Error in upload file to S3:" + e.getMessage());
            return "Error reading contents of the file";
        }
        return null;
    }
}
