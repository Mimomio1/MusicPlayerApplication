package com.proj.music.service;

import java.util.List;

import com.proj.music.dto.ReviewDTO;
import com.proj.music.entity.Reviews;

import se.michaelthelin.spotify.model_objects.specification.Track;

public interface ReviewService {
	String updateReviewById(long reviewId, String entityId, String entityType, Reviews review);

	String deleteReviewById(long reviewId, String entityType, String entityId);

	ReviewDTO getReviewById(long reviewid, String entityType, String entityId);

	Reviews addReview(Reviews review, String entityId, String entityType, String userId, Object object);
	
//	List<Reviews> getReviewsBySongId(String spotifyId);
	
	List<ReviewDTO> getReviews(String entityType, String entityId);
}