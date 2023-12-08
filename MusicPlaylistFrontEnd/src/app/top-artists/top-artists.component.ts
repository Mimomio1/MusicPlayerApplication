import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TopArtistsService } from './top-artists.service';
import { Artist } from './Artist';

@Component({
  selector: 'app-top-artists',
  templateUrl: './top-artists.component.html',
  styleUrls: ['./top-artists.component.css'],
})
export class TopArtistsComponent implements OnInit {
  topArtists: Artist[] = [];
  userId: string;
  accessToken: string;

  constructor(private route: ActivatedRoute, private topArtistsService: TopArtistsService) 
  { 
    this.userId = ''; 
    this.accessToken = '';
  }

  ngOnInit(): void {
    // Capture the userId from the URL parameters
    this.route.params.subscribe((params) => {
      this.userId = params['id'];
      this.accessToken = params['accessToken'];
      console.log('UserId:', this.userId); // Add this line to log the value of userId
      this.getTopArtists();
    });
  }

  getTopArtists(): void {
    if (this.userId) {
      this.topArtistsService.getTopArtists(this.userId).subscribe(
        (artists) => {
          this.topArtists = artists;
        },
        (error) => {
          console.error('Error fetching top artists:', error);
        }
      );
    } else {
      console.error('UserId is undefined');
    }
  }
}
