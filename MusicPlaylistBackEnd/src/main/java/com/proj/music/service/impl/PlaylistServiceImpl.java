package com.proj.music.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proj.music.entity.Playlists;
import com.proj.music.entity.Songs;
import com.proj.music.entity.Users;
import com.proj.music.exceptions.ResourceNotFoundException;
import com.proj.music.repository.PlaylistRepository;
import com.proj.music.repository.SongRepository;
import com.proj.music.repository.UserRepository;
import com.proj.music.service.PlaylistService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.Track;

@Service
public class PlaylistServiceImpl implements PlaylistService {

	@Autowired
	private PlaylistRepository playlistRepository;

	@Autowired
	private UserRepository userRespository;

	@Autowired
	private SongRepository songRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	@Override
	public String updatePlaylistById(String id, Playlist playlist) {
		Optional<Playlists> playlist1 = playlistRepository.findPlaylistBySpotifyId(id);
		if (playlist1.isPresent()) {
			playlist1.get().setName(playlist.getName());
			playlist1.get().setDescription(playlist.getDescription());
		}
		playlistRepository.save(playlist1.get());
		return "Playlist has been updated";
	}

	@Override
	public String deletePlaylistById(long id) {
		Optional<Playlists> playlist1 = playlistRepository.findById(id);
		if (playlist1.isPresent()) {
			playlistRepository.deleteById(id);
		}
		return "Playlist has been deleted";
	}

	@Override
	public Playlists getPlaylistById(long id) {
		return playlistRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist Object has not been found."));
	}

	@Override
	public String addPlaylist(Playlist playlist, String userId) {
		Optional<Users> optionalUser = Optional.of(userRespository.findByRefId(userId));
//		Playlist spotifyPlaylist = spotifyService.getPlaylist(playlistId);
        // Convert Spotify Playlist images to byte arrays
//        List<byte[]> imageDatas = convertImagesToByteArrays(spotifyPlaylist.getImages());
		if (optionalUser.isPresent()) {
			Users user = optionalUser.get();

			// Set playlist properties
			Playlists playlists = new Playlists();
			playlists.setName(playlist.getName());
			playlists.setDescription(playlist.getDescription());
//			playlists.setImages(playlist.getImages());
			playlists.setSpotifyId(playlist.getId());
//	        if (!imageDatas.isEmpty()) {
//	            playlists.setImageData(imageDatas.get(0)); // Assuming you are storing only the first image
//	        }
			// Add the playlist to the user's playlists
			user.getPlaylists().add(playlists);

			// Save changes
			playlistRepository.save(playlists);

			return "Playlist has been added";
		} else {
			return "User not found";
		}
	}

	private List<byte[]> convertImagesToByteArrays(Image[] images) {
        List<byte[]> imageDatas = new ArrayList<>();

        for (Image image : images) {
            // Assuming you have a method to fetch image data as byte array
//            byte[] imageData = fetchImageDataFromUrl(image.getUrl());
//            if (imageData != null) {
//                imageDatas.add(imageData);
//            }
        }

        return imageDatas;
    }
	
	@Override
	public List<Playlists> getPlaylists() {
		return playlistRepository.findAll();
	}

	@Override
	@Transactional
	public String deletePlaylistBySpotifyId(String spotifyPlaylistId) {
		List<Playlists> playlists = entityManager
				.createQuery("SELECT p FROM Playlists p WHERE p.spotifyId = :spotifyPlaylistId", Playlists.class)
				.setParameter("spotifyPlaylistId", spotifyPlaylistId).getResultList();

		if (!playlists.isEmpty()) {
			for (Playlists playlist : playlists) {
				// Disassociate from users
				List<Users> users = playlist.getUsers();
				users.forEach(user -> user.getPlaylists().remove(playlist));

				// Manually delete associated records from user_playlist
				Query query = entityManager
						.createNativeQuery("DELETE FROM user_playlist WHERE playlist_id = :playlistId");
				query.setParameter("playlistId", playlist.getId());
				query.executeUpdate();

				// Now, delete the playlist
				playlistRepository.delete(playlist);
			}

			return "Playlist has been deleted for user";
		} else {
			return "No playlist found with Spotify ID: " + spotifyPlaylistId;
		}
	}

	@Override
	public Playlists getPlaylistBySpotifyId(String spotifyPlaylistId) {
		Optional<Playlists> playlist1 = playlistRepository.findPlaylistBySpotifyId(spotifyPlaylistId);

		if (playlist1.isPresent()) {

			Playlists plist = playlist1.get();

			return plist;

		} else {

			return null;
		}

	}

	@Transactional
	public String addSongToPlaylist(String playlistId, Track track) {
		Optional<Playlists> optionalPlaylist = playlistRepository.findPlaylistBySpotifyId(playlistId);
		List<Songs> newSongs = new ArrayList<Songs>();
		if (optionalPlaylist.isPresent()) {

			// Set playlist properties
			Songs song = new Songs();
			song.setName(track.getName());
			song.setUris(track.getUri());
			song.setDuration(track.getDurationMs());
			song.setSpotifyId(track.getId());
			newSongs.add(song);
			optionalPlaylist.get().getSongs().add(song);

			// Save changes
			songRepository.save(song);
			playlistRepository.save(optionalPlaylist.get());

			return "New Song has been added to playlist";
		} else {
			return "User not found";
		}
	}

	@Transactional
	public String deleteSongFromPlaylist(String songId) {
		List<Songs> songs = entityManager
				.createQuery("SELECT s FROM Songs s WHERE s.spotifyId = :spotifyId", Songs.class)
				.setParameter("spotifyId", songId).getResultList();

		if (!songs.isEmpty()) {
			for (Songs song : songs) {
				// Disassociate from playlists
				List<Playlists> playlists = song.getPlaylists();
				playlists.forEach(playlist -> playlist.getSongs().remove(song));

				// Manually delete associated records from playlist_songs
				Query query = entityManager.createNativeQuery("DELETE FROM playlist_songs WHERE song_id = :songId");
				query.setParameter("songId", song.getId());
				query.executeUpdate();

				// Now, delete the song
				songRepository.delete(song);
			}

			return "Songs have been deleted from the playlist";
		} else {
			return "No songs found with Spotify ID " + songId;
		}
	}

	@Transactional
	public void deleteUserPlaylistAssociation(long userId, long playlistId) {
		String sql = "DELETE FROM user_playlist WHERE user_id = :userId AND playlist_id = :playlistId";

		int deletedRows = entityManager.createNativeQuery(sql).setParameter("userId", userId)
				.setParameter("playlistId", playlistId).executeUpdate();

		System.out.println("Deleted rows: " + deletedRows);
	}
	
	public Boolean isPlaylistExist(String spotifyId) {
		return playlistRepository.existsBySpotifyId(spotifyId);
	}
}