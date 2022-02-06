package com.bijenkorf.assignment.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.bijenkorf.assignment.Error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class AWSBucketService {

	private final AmazonS3 s3Client;

	@Value("${aws.s3.endpoint}")
	private String endpointUrl;
	@Value("${aws.bucket.name}")
	private String bucketName;
	@Value("${aws.accesskey}")
	private String awsAccessKey;
	@Value("${aws.secretkey}")
	private String awsSecretKey;

	private int counter = 0;

	public AWSBucketService(AmazonS3 s3Client) {
		this.s3Client = s3Client;
	}

	public AmazonS3 configureAWSClient() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		return AmazonS3ClientBuilder
				.standard()
				.withRegion("eu-central-1")
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.build();
	}

	public static String getDirectoryStructure(String predefinedType, String filename) {

		StringBuilder pathBuilder = new StringBuilder();

		final String extension = FilenameUtils.getExtension(filename);
		String filenameWithoutExtension = FilenameUtils.removeExtension(filename);
		filenameWithoutExtension = filenameWithoutExtension.replace("/","_");

		pathBuilder.append("/").append(predefinedType).append("/");

		if (filenameWithoutExtension.length() >= 4) {
			pathBuilder.append(filenameWithoutExtension, 0, 4).append("/");
		}
		if (filenameWithoutExtension.length() >= 8) {
			pathBuilder.append(filenameWithoutExtension, 4, 8).append("/");
		}
		pathBuilder.append(filenameWithoutExtension);

		return pathBuilder
				.append(".")
				.append(extension)
				.toString();
	}

	public String getPathOfObjectInBucket(String imageType, String filename) {

		String directoryPathOfImage = getDirectoryStructure(imageType, filename);

		try {
			if (s3Client.doesObjectExist(bucketName, directoryPathOfImage)) {
				return directoryPathOfImage;
			}
		} catch (AmazonS3Exception e) {
			e.printStackTrace();
			throw new NotFoundException("An exception occured while checking if the object " + filename + " exists in the bucket");
		}

		return "";
	}

	public S3Object downloadFile(String filePath) {
		S3Object s3object = new S3Object();

		try {
			s3object = this.s3Client.getObject(new GetObjectRequest(bucketName, filePath));
		} catch (AmazonServiceException e) {
			log.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
			log.info("Error Message:    " + e.getMessage());
			log.info("HTTP Status Code: " + e.getStatusCode());
			log.info("AWS Error Code:   " + e.getErrorCode());
		} catch (AmazonClientException ace) {
			log.info("Caught an AmazonClientException: ");
			log.info("Error Message: " + ace.getMessage());
		}

		return s3object;
	}

	public void uploadFile(String filename, File file) {
		this.s3Client.putObject(bucketName, filename, file);
	}

	public void deleteImage(String predefinedTypeName, String reference) {
		String directoryOfImage = getDirectoryStructure(predefinedTypeName, reference);
		s3Client.deleteObject(new DeleteObjectRequest(bucketName, directoryOfImage));
	}
}
