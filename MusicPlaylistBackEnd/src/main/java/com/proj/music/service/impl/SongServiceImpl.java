package com.proj.music.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proj.music.entity.Artists;
import com.proj.music.entity.Songs;
import com.proj.music.entity.Users;
import com.proj.music.exceptions.ResourceNotFoundException;
import com.proj.music.repository.ArtistRepository;
import com.proj.music.repository.SongRepository;
import com.proj.music.repository.UserRepository;
import com.proj.music.service.SongService;

import jakarta.transaction.Transactional;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

@Service
public class SongServiceImpl implements SongService {

	@Autowired
	private SongRepository songRepository;

	@Autowired
	private ArtistRepository artistRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public String updateSongById(long id, Songs song) {
		Optional<Songs> song1 = songRepository.findById(id);
		if (song1.isPresent()) {
			song1.get().setId(song.getId());
			song1.get().setName(song.getName());
			song1.get().setDuration(song.getDuration());
		}
		return "Song has been updated";
	}

	@Override
	public String deleteSongById(long id) {
		Optional<Songs> song1 = songRepository.findById(id);
		if (song1.isPresent()) {
			songRepository.deleteById(id);
		}
		return "Song has been deleted";
	}

	@Override
	public Songs getSongById(long id) {
		return songRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Song Object has not been found."));
	}
	
	public Songs getSongBySpotifyId(String spotifyId) {
		return songRepository.findSongBySpotifyId(spotifyId)
				.orElseThrow(() -> new ResourceNotFoundException("Song Object has not been found."));
	}

	@Override
	@Transactional
	public String addSong(Track song, String userId) {
		
		Optional<Users> newUser = Optional.of(userRepository.findByRefId(userId));
		// adds the songs
		Songs newSongs = createSongFromSpotifyData(song, newUser.get());
		// creates a list of artist
		List<Artists> listOfArtists = new ArrayList<>();
		// loops through list of artist found for song and adds to list of artist to list
		for(ArtistSimplified artistSimplified : song.getArtists()) {
			Artists artist = createArtistFromSpotifyData(artistSimplified);
			listOfArtists.add(artist);
		}
		
		songRepository.save(newSongs);
		// saves each artist for the song in the list to the artist table

		listOfArtists.forEach(artist -> {
			artist.getSongs().add(newSongs);
			newSongs.getArtists().add(artist);
			artistRepository.save(artist);
		});
		
		newUser.get().getSongs().add(newSongs);
		
		userRepository.save(newUser.get());
		
		return "Song has been added";
	}
	
	public Artists createArtistFromSpotifyData(ArtistSimplified artistSimplified) {
	    Artists artist = new Artists();
	    artist.setName(artistSimplified.getName());
	    artist.setSpotifyId(artistSimplified.getId());
	    artist.setHref(artistSimplified.getHref());
	    artist.setUri(artistSimplified.getUri());
	    return artist;
	}
	
	public Songs createSongFromSpotifyData(Track track, Users user) {
	    Songs newSongs = new Songs();
	    newSongs.setName(track.getName());
	    newSongs.setSpotifyId(track.getId());
	    newSongs.setUris(track.getUri());
	    newSongs.setDuration(track.getDurationMs());
	    newSongs.setPreviewUrl(track.getPreviewUrl());
	    newSongs.getUsers().add(user);
	    return newSongs;
	}

	@Override
	public List<Songs> getSongs() {
		return songRepository.findAll();
	}
	
	@Transactional
	public String deleteBySpotifyId(String spotifyId)
	{
		songRepository.deleteBySpotifyId(spotifyId);
		return "Song has been deleted";
	}

	@Override
	public Boolean existsBySpotifyId(String spotifyId) {
		return songRepository.existsBySpotifyId(spotifyId);
	}
}
