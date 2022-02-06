package com.bijenkorf.assignment.controller;

import com.bijenkorf.assignment.model.Image;
import com.bijenkorf.assignment.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController implements IImageController {

	private final ImageService imageService;

	public ImageController(ImageService imageService) {
		this.imageService = imageService;
	}

	@GetMapping("/show/{predefinedTypeName}/{dummySeoName}")
	public ResponseEntity<Image> getImage(@PathVariable String predefinedTypeName,
										  @PathVariable(value="dummySeoName", required=false) String dummySeoName,
										  @RequestParam("reference") String reference) {

		final var donwloadedImage = imageService.getOptimizedImage(predefinedTypeName, reference);

		return ResponseEntity.ok().body(donwloadedImage);
	}

	@GetMapping("/flush/{predefinedTypeName}")
	public ResponseEntity<Void> flushImage(@PathVariable("predefinedTypeName") String predefinedTypeName,
						   @RequestParam(value = "reference") String reference) {
		imageService.deleteImage(predefinedTypeName, reference);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
