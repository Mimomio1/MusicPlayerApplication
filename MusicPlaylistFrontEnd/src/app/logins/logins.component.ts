import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-logins',
  templateUrl: './logins.component.html',
  styleUrls: ['./logins.component.css']
})
export class LoginsComponent {
  constructor(private http: HttpClient) {}

  getSpotifyUserLogin() {
    this.http.get('http://localhost:8080/api/login').subscribe(
      (response: any) => {
        window.location.replace(response.url);
      },
      (error) => {
        console.error('HTTP request error:', error);
      }
    );
  }
  
}
