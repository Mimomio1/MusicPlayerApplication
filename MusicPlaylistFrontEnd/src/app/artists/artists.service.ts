import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ArtistsService {
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  searchArtist(userId: string, query: string): Observable<any[]> {
    const url = `${this.baseUrl}/api/search?userId=${userId}&q=${query}&type=artist`;
    return this.http.get<any[]>(url);
  }

  getAlbumsByArtistId(artistId: string, userId: string): Observable<any[]> {
    const url = `${this.baseUrl}/api/artists/${artistId}/albums?userId=${userId}`;
    return this.http.get<any[]>(url);
  }

  getAlbumTracks(albumId: string, userId: string): Observable<any[]> {
    const url = `${this.baseUrl}/api/albums/${albumId}/tracks?userId=${userId}`;
    return this.http.get<any[]>(url);
  }
  
  saveAlbums(userId: string, albumIds: string[]): Observable<any> {
    const url = `${this.baseUrl}/api/user/albums`;
    const params = new HttpParams()
        .set('userId', userId)
        .set('albumIds', albumIds.join(','));

    if (userId && albumIds) {
        return this.http.put(url, {}, { params, responseType: 'text' });
    } else {
        // Handle invalid input or missing userId, e.g., show a validation message
        return throwError('Invalid input or missing userId');
    }
  }
}
