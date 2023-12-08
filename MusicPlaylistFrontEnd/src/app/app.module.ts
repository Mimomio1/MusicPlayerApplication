import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // Add this line

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginsComponent } from './logins/logins.component';
import { HomeComponent } from './home/home.component';
import { PlaylistComponent } from './playlist/playlist.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ReviewsComponent } from './reviews/reviews.component';
import { ArtistsComponent } from './artists/artists.component';
import { PlayerComponent } from './player/player.component';
import { SongComponent } from './song/song.component';
import { AuthenticationComponent } from './authentication/authentication.component';

import { AuthService } from './authentication/auth.service';
import { AlbumsComponent } from './albums/albums.component';
@NgModule({
  declarations: [
    AppComponent,
    LoginsComponent,
    HomeComponent,
    PlaylistComponent,
    ReviewsComponent,
    ArtistsComponent,
    SongComponent,
    AuthenticationComponent,
    AlbumsComponent
  ],
  imports: [
    
    BrowserModule,
    FormsModule, // Add this line
    AppRoutingModule, NgbModule
  ],
  providers: [AuthService],
  bootstrap: [AppComponent]
})
export class AppModule { }
