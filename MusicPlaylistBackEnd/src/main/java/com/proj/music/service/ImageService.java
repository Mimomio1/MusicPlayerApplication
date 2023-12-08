package com.proj.music.service;

import org.springframework.web.multipart.MultipartFile;

import com.proj.music.entity.ImageModel;

public interface ImageService {
	String uploadImage(MultipartFile file);
	
	ImageModel getImage(String imageName);
}
