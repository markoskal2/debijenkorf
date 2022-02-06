package com.bijenkorf.assignment.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PredefinedImageTypes {

	private String name;
	private int height;
	private int width;
	private int quality;
	private ScaleType scaleType;
	private String fillColor;
	private Type type;

	private List<String> imageTypes = initImageTypes();

	public Image resize(Image originalImage) {
		log.info("TODO - This method would resize originalImage {} according to requirements",
				originalImage);
		return null;
	}

	public Image optimize(Image originalImage) {
		log.info("TODO - This method would optimize the originalImage {} according to requirements",
				originalImage);
		return null;
	}

	public List<String> initImageTypes() {
		imageTypes = new ArrayList<>();
		imageTypes.add("DETAIL_LARGE");
		imageTypes.add("THUMBNAIL");

		return imageTypes;
	}

	public boolean containsImageType(String imageType) {
		return imageTypes.contains(imageType);
	}

}
