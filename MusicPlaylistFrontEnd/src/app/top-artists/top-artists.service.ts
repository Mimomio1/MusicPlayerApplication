import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppComponent } from '../app.component';
import { Artist } from './Artist';

@Injectable({
  providedIn: 'root'
})
export class TopArtistsService {
  private apiURL = `${AppComponent.serverRoot}/api`;
  constructor(private http: HttpClient) {}

  getTopArtists(userId: any): Observable<Artist[]> {
    // const params = new HttpParams().set('userId', userId);
    return this.http.get<Artist[]>(this.apiURL + `/user-top-songs?id=${userId}`);
  }
}
