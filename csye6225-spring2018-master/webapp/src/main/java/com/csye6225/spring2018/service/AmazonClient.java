package com.csye6225.spring2018.service;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service

public class AmazonClient {
    final static Logger logger = LoggerFactory.getLogger(AmazonClient.class);

    private AmazonS3 s3client;

    @Value("${endpointUrl}")
    private String endpointUrl;

    @Value("${bucketName}")
    private String bucketName;

    /*@Value("${accessKey}")
    private String accessKey;

    @Value("${secretKey}")
    private String secretKey;*/

    @PostConstruct
    private void initializeAmazon() {
        //AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        //this.s3client = new AmazonS3Client(credentials);

        s3client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();
    }

    public String uploadFile(MultipartFile multipartFile) {

        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            logger.info("file in uploadFile: " + file.getAbsolutePath());
            logger.info("file in uploadFile path: " + file.getPath());
            String fileName = generateFileName(multipartFile);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            //s3client.putObject(new PutObjectRequest(bucketName, fileName,multipartFile.getInputStream(),new ObjectMetadata()));
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File("/home/ubuntu/images/" +file.getOriginalFilename());
        logger.info("Path : "+convFile.getPath());
        System.out.println("Path : "+convFile.getPath());
        logger.info("Absolute Path  :"+ convFile.getAbsolutePath());
        System.out.println("Absolute Path  :"+ convFile.getAbsolutePath());
        logger.info("File object: " + file);
        System.out.println("File object: " + file);
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        file.transferTo(convFile);
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        logger.info("Bucketname: " + bucketName);
        logger.info("fileName: " + fileName);
        logger.info("file: " + file);
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "Successfully deleted";
    }
}
