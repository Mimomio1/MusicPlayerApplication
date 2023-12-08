package com.proj.music.service;

import java.util.List;

import com.proj.music.entity.Albums;

import se.michaelthelin.spotify.model_objects.specification.Album;

public interface AlbumService {
	String updateAlbumById(long id, Albums album);

	String deleteAlbumBySpotifyId(String spotifyId);

	Albums getAlbumById(long id);

	String addAlbum(Album album, String userId);

	List<Albums> getAlbums();
}
 