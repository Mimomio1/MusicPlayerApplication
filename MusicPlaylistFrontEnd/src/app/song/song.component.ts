
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { SongService } from './song.service';
import { AuthService } from '../authentication/auth.service';

interface ApiResponse {
  albums: any;
  artists: any;
  episodes: any;
  playlists: any;
  shows: any;
  tracks: {
    href: string;
    items: any[];
    limit: number;
    next: string;
    offset: number;
    // Add any other properties if needed
  };
}

@Component({
  selector: 'app-song',
  templateUrl: './song.component.html',
  styleUrls: ['./song.component.css']
})
export class SongComponent implements OnInit{

  nameOfPlaylist: string = '';
  userId: string = '';
  playlistId: string = ''; 
  updatedPlaylistName: string = ''; // new property
  updatedPlaylistDescription: string = ''; // new property
  playlists: any[] = [];
  selectedPlaylistId: string = ''; // new property
  selectedDeletePlaylistId: string = ''; // new property

  searchQuery: string = '';
  searchResults: any[] = [];
  selectedSongId: string = '';

  selectedSongImage: string | undefined;
  selectedPlaylist: any = {}; // New property to store the selected playlist details (New Update)
  playlistSongs: any[] = []; // New property to store playlist songs(New Update)

  accessToken: string | null | undefined;

  private searchSubject = new Subject<string>();

  

  constructor(
    private songService: SongService,
    private route: ActivatedRoute,
    private router: Router, // Inject the Router service
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.userId = params['id'];
      this.accessToken = params['accessToken'];
      this.loadPlaylists();
    });

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => this.songService.searchSongs(this.userId, query))
    ).subscribe(
      (response: any) => {
        console.log('API Response:', response);
    
        // Check if there are items in the tracks property
        if (response.tracks && response.tracks.items) {
          this.searchResults = response.tracks.items;
          console.log('Debug: Search Results -', this.searchResults);
        } else {
          console.warn('API response is missing expected structure:', response);
        }
      },
      error => {
        console.error('Failed to search songs:', error);
      }
    );
  }

  navigateToReviews() {
    // Assuming you have the required information for entityType, entityId, and songId
    const entityType = 'Songs';
    const songId = this.selectedSongId; // Replace with the actual way you get the song id
    const id = this.userId;
    // Navigate to the review page with parameters
    this.router.navigate(['/reviews'], {
      queryParams: {
        userId: id,
        accessToken: this.accessToken,
        entityId: songId,
        entityType: entityType,
      },
    });
  }
  // Update the URL when a playlist is selected
  onPlaylistSelected(): void {
    // Update the URL with the selected playlistId
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { playlistId: this.selectedPlaylistId },
      queryParamsHandling: 'merge'
    });

    // Set the playlistId property
    this.playlistId = this.selectedPlaylistId;
    this.loadPlaylistSongs(this.selectedPlaylistId);// New Update
  }

  private loadPlaylists(): void {
    this.songService.getPlaylists(this.userId).subscribe(
      playlists => {
        // Extract only the names of the playlists
        this.playlists = playlists.map(playlist => ({ playlistName: playlist.name, spotifyId: playlist.id }));
        
        // Extract playlistId from the URL
        this.route.queryParams.subscribe(params => {
          this.selectedPlaylistId = params['playlistId'] || ''; // Default to an empty string
        });
      },
      error => {
        console.error('Failed to load playlists:', error);
      }
    );
  }

  onDeletePlaylistSelected(): void {
    // Update the URL with the selected playlistId for deletion
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { playlistId: this.selectedDeletePlaylistId },
      queryParamsHandling: 'merge'
    });

    this.playlistId = this.selectedDeletePlaylistId;
  }

  deletePlaylist(): void {
    if (this.userId && this.selectedDeletePlaylistId) {
      this.songService.deletePlaylist(this.userId, this.selectedDeletePlaylistId).subscribe(
        response => {
          console.log('Playlist deleted successfully:', response);
        },
        error => {
          console.error('Failed to delete playlist:', error);
        }
      );
    } else {
      // Handle invalid input or missing userId/selectedDeletePlaylistId
      // e.g., show a validation message
    }
  }

  // Function to handle the search input
  searchSongs(): void {
    this.searchSubject.next(this.searchQuery);
    this.selectedSongId = ''; // Reset selectedSongId when a new search is performed
  }

  // Function to update the URL when a song is selected
  onSongSelected(): void {
    // Check if a song is selected
    if (this.selectedSongId) {
      // Update the URL with the selected trackId
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { trackId: this.selectedSongId },
        queryParamsHandling: 'merge'
      });
    }

    this.selectedSongImage = this.getSelectedSongImage();
  }

  // Updated method to add songs to the playlist
  addSongsToPlaylist(): void {
    if (this.playlistId && this.userId && this.selectedSongId) {
      this.songService.addSongsToPlaylist(this.playlistId, this.userId, this.selectedSongId).subscribe(
        response => {
          console.log('Songs added successfully:', response);
        },
        error => {
          console.error('Failed to add songs to playlist:', error);
        }
      );
    } else {
      // Handle invalid input or missing parameters, e.g., show a validation message
    }
  }

  deleteSongsFromPlaylist(): void {
    if (this.playlistId && this.userId && this.selectedSongId) {
      this.songService.deleteSongsFromPlaylistO(this.playlistId, this.userId, this.selectedSongId).subscribe(
        response => {
          console.log('Songs deleted from playlist successfully:', response);
        },
        error => {
          console.error('Failed to delete songs from playlist:', error);
        }
      );
    } else {
      // Handle invalid input or missing parameters, e.g., show a validation message
    }
  }

  private loadPlaylistSongs(playlistId: string): void {
    if (this.userId && playlistId) {
      this.songService.getPlaylistSongs(this.userId, playlistId).subscribe(
        (playlistSongsResponse: any) => {
          const playlistItems = playlistSongsResponse.tracks.items;

          if (playlistItems && playlistItems.length > 0) {
            this.playlistSongs = playlistItems.map((item: any) => ({
              id: item.track.id,
              name: item.track.name,
            }));

            console.log('Playlist Songs:', this.playlistSongs);
          } else {
            console.warn('No songs found in the playlist.');
          }
        },
        (error) => {
          console.error('Failed to load playlist songs:', error);
        }
      );
    }
  }

  // Method to get the image URL of the selected song
  getSelectedSongImage(): string | undefined {
    const selectedSong = this.searchResults.find(song => song.id === this.selectedSongId);

    if (selectedSong && selectedSong.album && selectedSong.album.images && selectedSong.album.images.length > 0) {
      return selectedSong.album.images[0].url;
    }

    // Return a default image URL or undefined if no image is found
    return 'path/to/default/image.jpg'; // Update with your default image path
  }



// Add this method to your component class
getSelectedSongName(): string | undefined {
  const selectedSong = this.searchResults.find(song => song.id === this.selectedSongId);
  return selectedSong ? selectedSong.name : '';
}

}