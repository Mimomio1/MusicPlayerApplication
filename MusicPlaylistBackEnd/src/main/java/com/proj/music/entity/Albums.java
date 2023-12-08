package com.proj.music.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Entity
@Table(name = "albums")
public class Albums {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "album_id")
	private int id;
	
	@Column(name = "spotifyAlbumId", nullable = false)
	private String spotifyId;

	@Column(name = "album_name", nullable = false)
	private String name;

	@Column(name = "genres")
	private String[] genres;

	@Temporal(TemporalType.DATE)
	@Column(name = "release_date")
	private String releaseDate;
	
	@Column(name = "uri")
	private String uri;
	
//	@Column(name = "images")
//	private Image[] images;

	@ManyToMany(mappedBy = "albums")
	private List<Artists> artists; // An album can be associated with multiple artists.

	@OneToMany(mappedBy = "albums")
	private List<Songs> songs; // for one album it contains multiple songs

	@OneToMany(mappedBy = "albums")
	private List<Reviews> reviews;
	
	@ManyToMany(mappedBy = "albums")
	private List<Users> users;
	
	public Albums() {
		super();
		this.songs = new ArrayList<>(); // Initialize the songs list
	}

	public Albums(int id, String name, String releaseDate, List<Artists> artists, List<Songs> songs) {
		super();
		this.id = id;
		this.name = name;
		this.releaseDate = releaseDate;
		this.artists = artists;
		this.songs = songs;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getGenres() {
		return genres;
	}

	public void setGenres(String[] genres) {
		this.genres = genres;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

	public List<Songs> getSongs() {
		if (users == null) {
			songs = new ArrayList<>();
		}
		return songs;
	}

	public void setSongs(List<Songs> songs) {
		this.songs = songs;
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

	public List<Users> getUsers() {
        if (users == null) {
        	users = new ArrayList<>();
        }
		return users;
	}

	public void setUsers(List<Users> users) {
		this.users = users;
	}
	
//	public Image[] getImages() {
//		return images;
//	}
//
//	public void setImages(Image[] images) {
//		this.images = images;
//	}

	@Override
	public String toString() {
	    return "Albums [id=" + id + ", spotifyId=" + spotifyId + ", name=" + name + ", genres="
	            + Arrays.toString(genres) + ", releaseDate=" + releaseDate + ", uri=" + uri + "]";
	}

}