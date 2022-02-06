package com.bijenkorf.assignment.Error;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@AllArgsConstructor
@NoArgsConstructor
public class NotFoundException extends RuntimeException {
	private String message;
}
