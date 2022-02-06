package com.bijenkorf.assignment.model;

import lombok.*;

import java.io.InputStream;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {

	private InputStream imageContent;

}
