package com.proj.music.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private long id;
    private String name;
    private String comment;
    private LocalDateTime datePosted;
    private double rating;
    
	public ReviewDTO() {
		super();
	}
	public ReviewDTO(long id, String name, String comment, LocalDateTime datePosted, double rating) {
		super();
		this.id = id;
		this.name = name;
		this.comment = comment;
		this.datePosted = datePosted;
		this.rating = rating;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public LocalDateTime getDatePosted() {
		return datePosted;
	}
	public void setDatePosted(LocalDateTime datePosted) {
		this.datePosted = datePosted;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	@Override
	public String toString() {
		return "ReviewDTO [id=" + id + ", name=" + name + ", comment=" + comment + ", datePosted=" + datePosted
				+ ", rating=" + rating + "]";
	}
    
    
}