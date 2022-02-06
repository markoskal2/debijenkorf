package com.bijenkorf.assignment.util;

import com.bijenkorf.assignment.model.Image;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

@Slf4j
public class ImageFileUtils {

	public static File getFileFromImage(Image image, String path){
		File file = new File(path);
		try {
			copyInputStreamToFile(image.getImageContent(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

}
