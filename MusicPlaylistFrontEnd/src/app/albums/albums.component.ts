import { Component, OnInit } from '@angular/core';
import { AlbumsService } from './albums.service';
import { AuthService } from '../authentication/auth.service';
import { ActivatedRoute, Router } from '@angular/router';

interface ApiResponse {
  albums: any;
  artists: any;
  episodes: any;
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
  selector: 'app-albums',
  templateUrl: './albums.component.html',
  styleUrls: ['./albums.component.css']
})
export class AlbumsComponent implements OnInit {
  yourButtonClickFunction(arg0: any) {

  }
    userId: string = '';
    description: string = '';
    accessToken: string | null | undefined;
    nameOfAlbums: string = '';
    albumId: string = '';
    albums: any[] = [];
    selectedAlbums: any = {};
    albumSongs: any[] = [];
    selectedAlbumId: string = '';
    selectedSongId: string = '';

    constructor(
      private albumsService: AlbumsService,
      private route: ActivatedRoute,
      private router: Router, // Inject the Router service
      private authService: AuthService
    ) {
      this.authService.accessToken$.subscribe((token) => {
        this.accessToken = token;
      });
    }

    ngOnInit(): void {
      this.route.queryParams.subscribe(params => {
        this.userId = params['id'];
        this.accessToken = params['accessToken'];
        this.loadAlbums();
      });

    }

    private loadAlbums(): void {
      this.albumsService.getAlbums(this.userId).subscribe(
        albums => {
          // Update the albums array with additional information
          this.albums = albums.map(album => {
            console.log('Album:', album);
            return {
              name: album.album.name,  // Use album.name instead of albumName
              image: album.album.images?.[0]?.url || null,  // Use album.images instead of albumImage
              spotifyId: album.album.id,
              description: album.album.label || '',  // You can modify this according to your data
            };
          });
    
          // Extract albumId from the URL
          this.route.queryParams.subscribe(params => {
            this.selectedAlbumId = params['albumId'] || ''; // Default to an empty string
          });
        },
        error => {
          console.error('Failed to load albums:', error);
        }
      );
    }
    
    private loadAlbumSongs(albumId: string): void {
      if (this.userId && albumId) {
        this.albumsService.getAlbumSongs(this.userId, albumId).subscribe(
          (albumSongsResponse: any) => {
            console.log('Album Songs Response:', albumSongsResponse);
    
            const albumItems = Array.isArray(albumSongsResponse) ? albumSongsResponse : [albumSongsResponse];
    
            if (albumItems && albumItems.length > 0) {
              this.albumSongs = albumItems.map((item: any) => ({
                name: item.name,
                image: item.album?.images?.[0]?.url || item.images?.[0]?.url || null,
              }));
              console.log('Album Songs:', this.albumSongs);
            } else {
              console.warn('No songs found in the album.');
            }
          },
          (error) => {
            console.error('Failed to load album songs:', error);
          }
        );
      }
    }
    
    
    

    
    
    

    onSelectAlbum(albumId: string): void {
      console.log('Selected AlbumId:', albumId);
    
      // Set the albumId property
      this.selectedAlbumId = albumId;
      
      // Update the URL with the selected albumId
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { albumId: this.selectedAlbumId },
        queryParamsHandling: 'merge'
      });
    
      // Load album songs
      this.loadAlbumSongs(this.selectedAlbumId);
    }

    onAlbumSelected(): void {
      // Update the URL with the selected playlistId
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { albumId: this.selectedAlbumId },
        queryParamsHandling: 'merge'
      });

      // Set the playlistId property
      this.albumId = this.selectedAlbumId;
      this.loadAlbumSongs(this.selectedAlbumId);// New Update
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
    }
}
