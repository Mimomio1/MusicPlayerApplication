import { Component } from '@angular/core';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css']
})
export class AuthenticationComponent {
  constructor(private authService: AuthService) {}

  handleAuthentication() {
    // Assuming you have obtained the access token from some source
    const accessToken = 'your-access-token'; // Replace with the actual token

    // Store the access token in the service
    this.authService.setAccessToken(accessToken);
  }

}

