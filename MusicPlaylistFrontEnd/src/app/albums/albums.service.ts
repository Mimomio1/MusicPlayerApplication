import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AlbumsService {
  private baseUrl = 'http://localhost:8080'; // Replace with your backend URL and port

  constructor(private http: HttpClient) {}

  getAlbums(userId: string): Observable<any[]> {
    const url = `${this.baseUrl}/api/user/albums?userId=${userId}`;
    return this.http.get<any[]>(url);
  }

  // New method to get songs for a playlist
  getAlbumSongs(userId: string, albumId: string): Observable<any[]> {
    const url = `${this.baseUrl}/api/albums/${albumId}/tracks?userId=${userId}`;
    return this.http.get<any[]>(url);
  }

}