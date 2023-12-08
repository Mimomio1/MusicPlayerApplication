package com.proj.music.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "songs")
public class Songs {

	@Id
	@Column(name = "song_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "song_name", nullable = false)
	private String name;

	@Column(name = "duration")
	private double duration;

	@Column(name = "preview_url")
	private String previewUrl;

	@ManyToOne
	@JoinColumn(name = "album_id")
	private Albums albums; 

//	@Column(name = "genres")
//	private String[] genres;

	@OneToMany(mappedBy = "songs", cascade = CascadeType.REMOVE)
	private List<Playlists> playlists; // A song can belong to multiple playlists

	@ManyToMany(mappedBy = "songs")
	private List<Artists> artists; // List of artists associated with this song

	@OneToMany(mappedBy = "songs")
	private List<Reviews> reviews; // For one artist there are many reviews

	@Column(name = "uris")
	private String uris;

	@Column(name = "spotifyId")
	private String spotifyId;

	@ManyToMany(mappedBy = "songs")
	private List<Users> users;

	public Songs() {
		super();
	}

	public Songs(long id, String name, double duration, String previewUrl, Albums albums, List<Playlists> playlists,
			List<Artists> artists, List<Reviews> reviews, String uris, String spotifyId) {
		super();
		this.id = id;
		this.name = name;
		this.duration = duration;
		this.previewUrl = previewUrl;
		this.albums = albums;
		this.playlists = playlists;
		this.artists = artists;
		this.reviews = reviews;
		this.uris = uris;
		this.spotifyId = spotifyId;
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

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

//	public String[] getGenres() {
//		return genres;
//	}
//
//	public void setGenres(String[] genres) {
//		this.genres = genres;
//	}

	public List<Playlists> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(List<Playlists> playlists) {
		this.playlists = playlists;
	}

	public List<Artists> getArtists() {
		if (artists == null) {
			artists = new ArrayList<>();
		}
		return artists;
	}

	public void setArtists(List<Artists> artists) {
		this.artists = artists;
	}

	public Albums getAlbums() {
		return albums;
	}

	public void setAlbums(Albums albums) {
		this.albums = albums;
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

	public String getUris() {
		return uris;
	}

	public void setUris(String uris) {
		this.uris = uris;
	}

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public List<Users> getUsers() {
		if (users == null) {
			users = new ArrayList<>();
		}
		return users;
	}

	public void setUsers(List<Users> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Songs [id=" + id + ", name=" + name + ", duration=" + duration + ", previewUrl=" + previewUrl
				+ ", albums=" + albums + ", playlists=" + playlists + ", artists=" + artists + ", reviews=" + reviews
				+ ", uris=" + uris + ", spotifyId=" + spotifyId + ", users=" + users + "]";
	}

}