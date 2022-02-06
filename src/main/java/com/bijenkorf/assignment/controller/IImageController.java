package com.bijenkorf.assignment.controller;

import com.bijenkorf.assignment.model.Image;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface IImageController {

	@GetMapping("/show/{type}")
	ResponseEntity<Image> getImage(
			@PathVariable String type,
			@PathVariable(value="dummySeoName", required=false) String dummySeoName,
			@RequestParam("reference") String reference);

	@GetMapping("/flush/{type}")
	ResponseEntity<Void> flushImage(
			@PathVariable String type,
			@RequestParam("reference") String reference);
}
