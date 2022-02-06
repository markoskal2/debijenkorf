package com.bijenkorf.assignment.service;

import com.bijenkorf.assignment.Error.NotFoundException;
import com.bijenkorf.assignment.model.Image;
import com.bijenkorf.assignment.model.PredefinedImageTypes;
import com.bijenkorf.assignment.model.ScaleType;
import com.bijenkorf.assignment.model.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static com.bijenkorf.assignment.util.ImageFileUtils.getFileFromImage;

@Slf4j
@Service
public class ImageService {

	private final AWSBucketService s3Service;
	private Image image;

	private final String ORIGINAL_IMAGE = "original_image";

	@Value("${service.awsS3Endpoint}")
	private String bucketName;
	@Value("${service.sourceRootUrl}")
	private String sourceRootUrl;

	public ImageService(AWSBucketService s3Service, Image image) {
		this.s3Service = s3Service;
		this.image = image;
	}

	public Image getOptimizedImage(final String predefinedTypeName, final String filename) {
		validateImage(predefinedTypeName, filename);
		createOptimizedImage(predefinedTypeName, filename);
		return image;
	}

	public void deleteImage(String predefinedTypeName, String reference) {
		s3Service.deleteImage(predefinedTypeName, reference);
	}

	private void createOptimizedImage(String predefinedTypeName, String filename) {

		String filePathInS3Bucket = s3Service.getPathOfObjectInBucket(predefinedTypeName, filename);

		if (filePathInS3Bucket.isEmpty()) {
			checkOriginalImage(predefinedTypeName, filename);
		}
		else {
			image = new Image(s3Service.downloadFile(filePathInS3Bucket).getObjectContent());
		}
	}

	private void checkOriginalImage(String predefinedTypeName, String filename) {

		PredefinedImageTypes predefinedImage = getPredefinedImage(predefinedTypeName);

		String filePath = s3Service.getPathOfObjectInBucket(ORIGINAL_IMAGE, filename);

		if (filePath.isEmpty()) {
			uploadOriginalImageToS3(filePath);
			checkOriginalImage(predefinedTypeName, filename);
		}
		else {
			uploadEditedImageToS3(predefinedTypeName, filePath, predefinedImage);
			createOptimizedImage(predefinedTypeName, filename);
		}
	}

	private void uploadEditedImageToS3(String predefinedTypeName, String originalFilePath,
									   PredefinedImageTypes predefinedImage) {

		Image originalImage = new Image(s3Service.downloadFile(originalFilePath).getObjectContent());

		originalImage = editOriginalImage(originalImage, predefinedImage);

		File fileToUpload = getFileFromImage(originalImage, originalFilePath);
		String editedImageFilePath = originalFilePath.replace(ORIGINAL_IMAGE, predefinedTypeName);

		s3Service.uploadFile(editedImageFilePath, fileToUpload);
	}

	private void uploadOriginalImageToS3(String filePath) {
		File file = new File(sourceRootUrl + filePath);
		s3Service.uploadFile(filePath, file);
	}

	private Image editOriginalImage(Image originalImage, PredefinedImageTypes predefinedImage){
		originalImage = predefinedImage.resize(originalImage);
		originalImage = predefinedImage.optimize(originalImage);
		return originalImage;
	}

	public PredefinedImageTypes getPredefinedImage(String type){
		if("thumbnail".equals(type)){
			return PredefinedImageTypes.builder()
					.name("THUMBNAIL")
					.height(20)
					.width(20)
					.quality(100)
					.scaleType(ScaleType.CROP)
					.fillColor("2C5E1A")
					.type(Type.JPG)
					.build();
		}
		else if("detailLarge".equals(type)){
			return PredefinedImageTypes.builder()
					.name("DETAIL_LARGE")
					.height(100)
					.width(100)
					.quality(1000)
					.scaleType(ScaleType.FILL)
					.fillColor("088FFA")
					.type(Type.PNG)
					.build();
		}

		return PredefinedImageTypes.builder().build();
	}

	protected void validateImage(String predefinedTypeName, String reference){
		PredefinedImageTypes predefinedImageTypes = PredefinedImageTypes.builder().name(reference).build();
		try {
			if (!predefinedImageTypes.containsImageType(predefinedTypeName)) {
				throw new NotFoundException("The predefined type " + predefinedTypeName + " is not defined");
			}

			File file = new File(reference);
			if (!file.isFile()) {
				throw new NotFoundException(predefinedTypeName + " is not a file");
			}

			if (!isFileIsAnImage(reference)) {
				throw new NotFoundException("The file " + reference + " is not an image");
			}
		} catch (NotFoundException e) {
			log.info(e.getMessage());
		}
	}

	protected boolean isFileIsAnImage(String filename) {
		try {
			java.awt.Image image = ImageIO.read(new File(filename));
			if (image == null) {
				return false;
			}
		} catch(IOException ex) {
			return false;
		}

		return true;
	}

}
