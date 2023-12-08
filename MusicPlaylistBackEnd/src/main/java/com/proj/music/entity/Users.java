package com.proj.music.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "users")
public class Users implements Serializable {

	private static final long serialVersionUID = 3937414011943770889L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private long id;

	@Column(name = "userName")
	private String userName;

	@Column(name = "email")
	private String email;

	@Column(name = "REF_ID")
	private String refId;

	@Column(name = "ACCESS_TOKEN", length = 3000)
	private String accessToken;

	@Column(name = "REFRESH_TOKEN", length = 3000)
	private String refreshToken;

	@Column(name = "Token_expiration")
	private Instant expiresAt;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Reviews> reviews;

	@ManyToMany
	@JoinTable(name = "user_playlist", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "playlist_id"))
	private List<Playlists> playlists;

	@ManyToMany
	@JoinTable(name = "user_albums", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "album_id"))
	@JsonBackReference
	private List<Albums> albums;
	
	@ManyToMany
	@JoinTable(name = "user_songs", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "song_id"))
	private List<Songs> songs;
	
	public Users() {
		super();
	}

	public Users(long id, String userName, String email, String refId, String accessToken, String refreshToken,
			Instant expiresAt, List<Playlists> playlists) {
		super();
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.refId = refId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresAt = expiresAt;
		this.playlists = playlists;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Playlists> getPlaylists() {
		if (playlists == null) {
			playlists = new ArrayList<>();
		}
		return playlists;
	}

	public void setPlaylists(List<Playlists> playlists) {
		this.playlists = playlists;
	}

	public String getRefid() {
		return refId;
	}

	public void setRefid(String refId) {
		this.refId = refId;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public List<Albums> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Albums> albums) {
		this.albums = albums;
	}
	
	public List<Songs> getSongs() {
		return songs;
	}

	public void setSongs(List<Songs> songs) {
		this.songs = songs;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public List<Reviews> getReviews() {
		if (reviews == null) {
            reviews = new ArrayList<>();
        }
        return reviews;
	}

	public void setReviews(List<Reviews> reviews) {
		this.reviews = reviews;
	}

	@Override
	public String toString() {
		return "Users [id=" + id + ", userName=" + userName + ", email=" + email + ", refId=" + refId + ", accessToken="
				+ accessToken + ", refreshToken=" + refreshToken + ", expiresAt=" + expiresAt + ", reviews=" + reviews
				+ ", playlists=" + playlists + ", albums=" + albums + ", songs=" + songs + "]";
	}

}