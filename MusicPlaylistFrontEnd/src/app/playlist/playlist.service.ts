import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {
  private baseUrl = 'http://localhost:8080'; // Replace with your backend URL and port

  constructor(private http: HttpClient) {}

  createPlaylist(url: string, playlistData: any): Observable<any> {

    return this.http.post(`${this.baseUrl}${url}`, playlistData, { responseType: 'text' });
  }
  
  updatePlaylist(userId: string, playlistId: string, playlistData: any): Observable<any> {
    const url = `${this.baseUrl}/api/user/playlist/${playlistId}?userId=${userId}`;

    return this.http.put(url, playlistData, { responseType: 'text' });
  }
 
  getPlaylists(userId: string): Observable<any[]> {
    const url = `${this.baseUrl}/api/users/${userId}/playlists`;
    return this.http.get<any[]>(url);
  }
  
  deletePlaylist(userId: string, playlistId: string): Observable<any> {
    const url = `${this.baseUrl}/api/delete-playlist/users/${playlistId}?userId=${userId}`; 
    return this.http.delete(url, { responseType: 'text' });
  }
  
// Updated searchSongs method to accept userId and query
searchSongs(userId: string, query: string): Observable<any[]> {
  const url = `${this.baseUrl}/api/search?userId=${userId}&q=${query}&type=track`;
  return this.http.get<any[]>(url);
}

 // New method to add songs to a playlist
 // New method to add songs to a playlist
addSongsToPlaylist(playlistId: string, userId: string, songId: string): Observable<any> {
  const url = `${this.baseUrl}/api/playlists/${playlistId}/songs?userId=${userId}&songUri=spotify:track:${songId}`;
  return this.http.post(url, { responseType: 'text' });
}
deleteSongsFromPlaylistO(playlistId: string, userId: string, songId: string): Observable<any> {
  const url = `${this.baseUrl}/api/playlists/${playlistId}/songs?userId=${userId}&songUri=spotify:track:${songId}`; 
  return this.http.delete(url, { responseType: 'text' });
}

// New method to get songs for a playlist
getPlaylistSongs(userId: string, playlistId: string): Observable<any[]> {
  const url = `${this.baseUrl}/api/playlists/${playlistId}?userId=${userId}`;
  return this.http.get<any[]>(url);
}

getImage(imageName: string): Observable<any[]> {
  //Make a call to Sprinf Boot to get the Image Bytes.
  return this.http.get<any[]>(`http://localhost:8080/api/get/${imageName}`);

}

uploadImage(file: File): Observable<any>{
  const formData: FormData = new FormData();
  formData.append('imageFile', file, file.name);

  const headers = new HttpHeaders();
  headers.append('Content-Type', 'multipart/form-data');
  headers.append('Accept', 'application/json');

  return this.http.post(`${this.baseUrl}/api/upload`, formData, { headers, observe: 'response' });
}

}