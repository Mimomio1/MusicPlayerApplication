import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  userId: string;
  accessToken: string;

  constructor(private route: ActivatedRoute) {
    this.userId = '';
    this.accessToken = '';
  }

  ngOnInit(): void {
    // Retrieve user ID and access token from route parameters
    this.route.queryParams.subscribe((params) => {
      this.userId = params['id'];
      this.accessToken = params['accessToken'];

      // You can perform actions here using userId and accessToken
      console.log('User ID:', this.userId);
      console.log('Access Token:', this.accessToken);
    });
  }
}
