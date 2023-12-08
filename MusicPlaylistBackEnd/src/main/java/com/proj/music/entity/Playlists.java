package com.proj.music.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "playlist")
public class Playlists {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "playlist_id")
	private long id;

	@Column(name = "playlist_name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "spotifyId")
	private String spotifyId;

	@Lob
	@Column(name = "image_data", columnDefinition = "BLOB", length = 5000)
	private byte[] imageData;

	@ManyToMany
	@JoinTable(name = "playlist_songs", joinColumns = @JoinColumn(name = "playlist_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))
	private List<Songs> songs; // A playlist can contain multiple songs

	@Column(name = "createdAt", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updatedAt", nullable = false, updatable = false)
	private LocalDateTime updatedAt;

	@ManyToMany(mappedBy = "playlists")
	private List<Users> users; // A playlist can be associated with multiple users

	@OneToMany(mappedBy = "playlist")
	private List<Reviews> reviews; // For one playlist there are many reviews

	public Playlists() {
		super();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Playlists(long id, String name, String description, String spotifyId, List<Songs> songs,
			LocalDateTime createdAt, LocalDateTime updatedAt, List<Users> users, List<Reviews> reviews) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.spotifyId = spotifyId;
		this.songs = songs;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.users = users;
		this.reviews = reviews;
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

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}

	public List<Songs> getSongs() {
		return songs;
	}

	public void setSongs(List<Songs> songs) {
		this.songs = songs;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<Users> getUsers() {
		return users;
	}

	public void setUsers(List<Users> users) {
		this.users = users;
	}

	public List<Reviews> getReviews() {
		return reviews;
	}

	public void setReviews(List<Reviews> reviews) {
		this.reviews = reviews;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	@Override
	public String toString() {
		return "Playlists [id=" + id + ", name=" + name + ", description=" + description + ", spotifyId=" + spotifyId
				+ ", songs=" + songs + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + ", users=" + users + ", reviews=" + reviews + "]";
	}

}