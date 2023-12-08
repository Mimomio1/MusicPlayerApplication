import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { HomeComponent } from './home/home.component';
import { LoginsComponent } from './logins/logins.component';
import { PlaylistComponent } from './playlist/playlist.component';
import { ReviewsComponent } from './reviews/reviews.component';
import { ArtistsComponent } from './artists/artists.component'; // Import the ArtistsComponent
import { PlayerComponent } from './player/player.component';
import { SongComponent } from './song/song.component';
import { AlbumsComponent } from './albums/albums.component';

const routes: Routes = [
  // Define the route for the "home" component
  {
    path: 'home',
    component: HomeComponent,
  },
  {
    path: 'login',
    component: LoginsComponent,
  },
  {
    path: 'playlist',
    component: PlaylistComponent, 
    pathMatch: 'full',
  },
  {
    path: 'player',
    component: PlayerComponent, 
    pathMatch: 'full',
  },
  {
    path: 'reviews',
    component: ReviewsComponent, 
    pathMatch: 'full',
  },
  {
    path: 'artists',
    component: ArtistsComponent,  // Add a route for ArtistsComponent
    pathMatch: 'full',
  },
  {
    path: 'song',
    component: SongComponent,  // Add a route for ArtistsComponent
    pathMatch: 'full',
  },
  {
    path: 'albums',
    component: AlbumsComponent,  // Add a route for ArtistsComponent
    pathMatch: 'full',
  },
  // Add other routes if needed

  // A generic route that redirects to 'home' if the URL doesn't match any defined route
  {
    path: '**',
    redirectTo: 'home',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes), HttpClientModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
