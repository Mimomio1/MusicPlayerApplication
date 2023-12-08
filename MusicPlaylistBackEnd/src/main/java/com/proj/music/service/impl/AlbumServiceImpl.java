package com.proj.music.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proj.music.entity.Albums;
import com.proj.music.entity.Artists;
import com.proj.music.entity.Songs;
import com.proj.music.entity.Users;
import com.proj.music.exceptions.ResourceNotFoundException;
import com.proj.music.repository.AlbumRepository;
import com.proj.music.repository.ArtistRepository;
import com.proj.music.repository.SongRepository;
import com.proj.music.repository.UserRepository;
import com.proj.music.service.AlbumService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

@Service
public class AlbumServiceImpl implements AlbumService {

	@Autowired
	private AlbumRepository albumRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ArtistRepository artistRepository;
	
	@Autowired
	private SongRepository songRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public String updateAlbumById(long id, Albums album) {
		Optional<Albums> album1 = albumRepository.findById(id);

		if (album1.isPresent()) {
			album1.get().setId(album.getId());
			album1.get().setName(album.getName());
			album1.get().setReleaseDate(album.getReleaseDate());
			album1.get().setArtists(album.getArtists());
		}

		return "Song has been updated";
	}

	@Override
	@Transactional
	public String deleteAlbumBySpotifyId(String spotifyId) {
		List<Albums> albums = entityManager
				.createQuery("SELECT a FROM Albums a WHERE a.spotifyId = :spotifyAlbumId", Albums.class)
				.setParameter("spotifyAlbumId", spotifyId).getResultList();

		if (!albums.isEmpty()) {
			for (Albums album : albums) {
				// Disassociate from users
				List<Users> users = album.getUsers();
				users.forEach(user -> user.getAlbums().remove(album));

				// Manually delete associated records from user_albums
				Query query = entityManager.createNativeQuery("DELETE FROM user_albums WHERE album_id = :albumId");
				query.setParameter("albumId", album.getId());
				query.executeUpdate();

				// Now, delete the album
				albumRepository.delete(album);
			}
			return "Album has been deleted for user";
		} else {
			return "No album found with Spotify ID: " + spotifyId;
		}
	}

	@Override
	public Albums getAlbumById(long id) {
		return albumRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Album Object has not been found."));
	}

	@Override
	@Transactional
	public String addAlbum(Album album, String userId) {
		Optional<Users> optionalUser = Optional.of(userRepository.findByRefId(userId));

	    if (optionalUser.isPresent()) {
	        Albums newAlbum = createAlbumFromSpotifyData(album, optionalUser.get());
	        List<Artists> newArtists = new ArrayList<Artists>();
	        List<Songs> newSongs = new ArrayList<Songs>();

	        if (album.getArtists() != null) {
	            for (ArtistSimplified artistSimplified : album.getArtists()) {
	                Artists artist = createArtistFromSpotifyData(artistSimplified);
	                newArtists.add(artist);
	            }
	        }
	        
	        if (album.getTracks() != null) {
	            for (TrackSimplified trackSimplified : album.getTracks().getItems()) {
	                Songs song = createSongsFromSpotifyData(trackSimplified);
	                newSongs.add(song);
	            }
	        }

	        // Save the album first
	        albumRepository.save(newAlbum);

	        // Save the artists after saving the album
	        newArtists.forEach(artist -> {
	            artist.getAlbums().add(newAlbum);
	            newAlbum.getArtists().add(artist);
	            artistRepository.save(artist);
	        });
	        
	        for (Songs song : newSongs) {
	            song.setAlbums(newAlbum);
	            newAlbum.getSongs().add(song);
	            songRepository.save(song);
	        }

	        optionalUser.get().getAlbums().add(newAlbum);

	        userRepository.save(optionalUser.get());

	        return "Album has been saved and added";
	    } else {
	        return "User not found";
	    }
	}

	private Albums createAlbumFromSpotifyData(Album album, Users user) {
	    Albums newAlbum = new Albums();
	    newAlbum.setName(album.getName());
	    newAlbum.setSpotifyId(album.getId());
	    newAlbum.setUri(album.getUri());
//	    newAlbum.setImages(album.getImages());
	    newAlbum.setGenres(album.getGenres());
	    newAlbum.setReleaseDate(album.getReleaseDate());
	    newAlbum.getUsers().add(user);
	    return newAlbum;
	}

	private Artists createArtistFromSpotifyData(ArtistSimplified artistSimplified) {
	    Artists artist = new Artists();
	    artist.setName(artistSimplified.getName());
	    artist.setSpotifyId(artistSimplified.getId());
	    artist.setHref(artistSimplified.getHref());
	    artist.setUri(artistSimplified.getUri());
	    return artist;
	}
	
	private Songs createSongsFromSpotifyData(TrackSimplified trackSimplified) {
	    Songs track = new Songs();
	    track.setName(trackSimplified.getName());
	    track.setSpotifyId(trackSimplified.getId());
	    track.setUris(trackSimplified.getUri());
	    track.setPreviewUrl(trackSimplified.getPreviewUrl());
	    track.setDuration(trackSimplified.getDurationMs());
	    return track;
	}

	@Override
	public List<Albums> getAlbums() {
		return albumRepository.findAll();
	}

}