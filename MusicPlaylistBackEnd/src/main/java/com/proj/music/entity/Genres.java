package com.proj.music.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "genres")
public class Genres {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "genre_id")
	private long id;

	@Column(name = "genre_name")
	private String name;

	@Column(name = "description")
	private String description;
	
	@Column(name = "genreSpotifyId")
	private String genreSpotifyId;
	
	// For one genre there are many artist
//	@OneToMany(mappedBy = "genres")
//	private Set<Artists> artists;
//	
//	@OneToMany(mappedBy = "playlists")
//	private Set<Playlists> playlists;
//	
//	@OneToMany(mappedBy = "albums")
//	private Set<Albums> albums;
//	
//	@OneToMany(mappedBy = "songs")
//	private Set<Songs> songs;

	public Genres() {
		super();
	}

	public Genres(long id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Genres [id=" + id + ", name=" + name + ", description=" + description + ", genreSpotifyId="
				+ genreSpotifyId + "]";
	}

}