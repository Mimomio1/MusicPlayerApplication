import { Component, OnInit } from '@angular/core';
import { ArtistsService } from './artists.service';
import { Subject } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { urlencoded } from 'express';
import { AuthService } from '../authentication/auth.service';

@Component({
  selector: 'app-artists',
  templateUrl: './artists.component.html',
  styleUrls: ['./artists.component.css']
})
export class ArtistsComponent implements OnInit {
  userId: string = '';
  searchQuery: string = '';
  selectedArtistId: string = '';
  selectedAlbumId: string = '';
  searchResults: any[] = [];
  albumResults: any[] = [];
  songResults: any[] = [];
  selectedSongId: string = ''; // Add this line
  private searchSubject = new Subject<string>();
  accessToken: any;
  albumIds: string = '';
  albums: any;
  albumsAvailable: boolean = false;
  constructor(private artistsService: ArtistsService, private route: ActivatedRoute, private router: Router, private authService: AuthService) {
    this.authService.accessToken$.subscribe((token) => {
      this.accessToken = token;
    });

    this.albums = [];
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.userId = params['id'];
      this.selectedArtistId = params['artistId'];
      this.selectedAlbumId = params['albumId'];
      
      // Fetch albums based on selectedArtistId if needed
     
    });

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => this.artistsService.searchArtist(this.userId, query))
    ).subscribe(
      (response: any) => {
        if (response.artists && response.artists.items) {
          this.searchResults = response.artists.items;
        } else {
          console.warn('API response is missing expected structure:', response);
        }
      },
      error => {
        console.error('Failed to search songs:', error);
      }
    );
  }

  saveAlbums(albumIds: string[]): void {
    if (this.userId && albumIds) {
        this.artistsService.saveAlbums(this.userId, albumIds).subscribe(
            (response: any) => {
                console.log('Albums saved successfully:', response);

                // Update your local list of albums
                albumIds.forEach(albumId => {
                    const index = this.albumResults.findIndex((album: { id: string; /* other properties */ }) => album.id === albumId);
                    if (index !== -1) {
                        this.albums.push(this.albumResults[index]);
                    }
                });
            },
            (error) => {
                console.error('Failed to save albums:', error);
            }
        );
    } else {
        // Handle invalid input or missing userId, e.g., show a validation message
    }
  } 

  navigateToReviews() {
    // Assuming you have the required information for entityType, entityId, and songId
    const entityType = 'Albums';
    const albumId = this.selectedAlbumId; // Replace with the actual way you get the song id
    const id = this.userId;
    this.router.navigate(['/reviews'], {
      queryParams: {
        userId: id,
        accessToken: this.accessToken,
        entityId: albumId,
        entityType: entityType,
      },
    });
  }

  // Function to handle the search input
  searchArtists(): void {
    this.searchSubject.next(this.searchQuery);
    this.selectedArtistId = ''; // Reset selectedArtistId when a new search is performed
    this.selectedAlbumId = ''; // Reset selectedAlbumId when a new search is performed
  }

  // Function to handle the fetching of albums
  
// Function to handle the fetching of albums
getAlbums(): void {
  if (this.selectedArtistId) {
    this.artistsService.getAlbumsByArtistId(this.selectedArtistId, this.userId).subscribe(
      (response: any) => {
        if (response && response.length > 0) {
          this.albumResults = response;
          // Set the flag to true when albums are available
          this.albumsAvailable = true;
        } else {
          console.warn('No albums found for the selected artist.');
          // Set the flag to false when no albums are found
          this.albumsAvailable = false;
        }
      },
      error => {
        console.error('Failed to get albums:', error);
        // Set the flag to false in case of an error
        this.albumsAvailable = false;
      }
    );
  } else {
    console.warn('No artist selected. Please select an artist first.');
    // Set the flag to false if no artist is selected
    this.albumsAvailable = false;
  }
}

  // Function to handle the selection of an artist
  onArtistSelected(): void {
    if (this.selectedArtistId) {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { artistId: this.selectedArtistId },
        queryParamsHandling: 'merge'
      });
   
    }
  }

  // Function to handle the selection of an album
  onAlbumSelected(): void {
    if (this.selectedAlbumId) {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { albumId: this.selectedAlbumId },
        queryParamsHandling: 'merge'
      });
    }
  }


  getSongsFromAlbum(): void {
    if (this.selectedAlbumId) {
      this.artistsService.getAlbumTracks(this.selectedAlbumId, this.userId).subscribe(
        (response: any) => {
          if (response && response.length > 0) {
            this.songResults = response;
          } else {
            console.warn('No songs found for the selected album.');
          }
        },
        error => {
          console.error('Failed to get songs:', error);
        }
      );
    } else {
      console.warn('No album selected. Please select an album first.');
    }
  }



  onSongSelected(): void {
    if (this.selectedSongId) {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { songId: this.selectedSongId },
        queryParamsHandling: 'merge'
      });
    }
  }

  
  // Function to get the image URL for the selected artist
  getArtistImage(artistId: string): string {
    // Adjust the logic to get the image URL for the artist
    // You may need to modify your service to provide the image URL
    // For example, assuming the service provides the image URL in the 'images' property
    const selectedArtist = this.searchResults.find(artist => artist.id === artistId);
    return selectedArtist ? selectedArtist.images[0].url : ''; // Assuming the first image in the array is the URL
  }

  // Function to get the image URL for the selected album
  getAlbumImage(albumId: string): string {
    // Adjust the logic to get the image URL for the album
    // You may need to modify your service to provide the image URL
    // For example, assuming the service provides the image URL in the 'images' property
    const selectedAlbum = this.albumResults.find(album => album.id === albumId);
    return selectedAlbum ? selectedAlbum.images[0].url : ''; // Assuming the first image in the array is the URL
  }



  // Function to get the name of the selected artist
getArtistName(artistId: string): string {
  const selectedArtist = this.searchResults.find(artist => artist.id === artistId);
  return selectedArtist ? selectedArtist.name : '';
}

// Function to get the name of the selected album
getAlbumName(albumId: string): string {
  const selectedAlbum = this.albumResults.find(album => album.id === albumId);
  return selectedAlbum ? selectedAlbum.name : '';
}

}
