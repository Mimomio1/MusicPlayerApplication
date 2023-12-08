package com.proj.music.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Entity
@Table(name = "artist")
public class Artists {

	@Id
	@Column(name = "artist_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "artistSpotifyId")
	private String spotifyId;

	@ManyToMany
	@JoinTable(name = "artist_song", joinColumns = @JoinColumn(name = "artist_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))
	private List<Songs> songs; // Artists can be associated with one or more songs.

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "artist_album", joinColumns = @JoinColumn(name = "artist_id"), inverseJoinColumns = @JoinColumn(name = "album_id"))
	private List<Albums> albums; // Artists can be associated with multiple albums.

	@Column(name = "genres")
	private String[] genres;

	@Column(name = "href")
	private String href;

	@Lob
	@Column(name = "images")
	private Image[] images;

	@Column(name = "popularity")
	private Integer popularity;

	@Column(name = "uri")
	private String uri;

	public Artists() {
		super();
	}

	public Artists(long id, String name, String spotifyId, List<Songs> songs, List<Albums> albums, String[] genres,
			String href, Image[] images, Integer popularity, String uri) {
		super();
		this.id = id;
		this.name = name;
		this.spotifyId = spotifyId;
		this.songs = songs;
		this.albums = albums;
		this.genres = genres;
		this.href = href;
		this.images = images;
		this.popularity = popularity;
		this.uri = uri;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public List<Songs> getSongs() {
        if (songs == null) {
        	songs = new ArrayList<>();
        }
		return songs;
	}

	public void setSongs(List<Songs> songs) {
		this.songs = songs;
	}

	public List<Albums> getAlbums() {
        if (albums == null) {
            albums = new ArrayList<>();
        }
        return albums;
	}

	public void setAlbums(List<Albums> albums) {
		this.albums = albums;
	}

	public String[] getGenres() {
		return genres;
	}

	public void setGenres(String[] genres) {
		this.genres = genres;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Image[] getImages() {
		return images;
	}

	public void setImages(Image[] images) {
		this.images = images;
	}

	public Integer getPopularity() {
		return popularity;
	}

	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
	    return "Artists [id=" + id + ", name=" + name + ", spotifyId=" + spotifyId + ", genres=" + Arrays.toString(genres)
	            + ", href=" + href + ", popularity=" + popularity + ", uri=" + uri + "]";
	}

}