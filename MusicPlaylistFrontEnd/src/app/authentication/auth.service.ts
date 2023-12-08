// auth.service.ts
import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private accessTokenSubject = new BehaviorSubject<string | null>(null);
  accessToken$ = this.accessTokenSubject.asObservable();

  constructor(private route: ActivatedRoute) {
    // Initialize the access token from the URL parameters on service creation
    this.route.queryParams.subscribe(params => {
      const accessToken = params['accessToken'];
      if (accessToken) {
        this.setAccessToken(accessToken);
      }
    });
  }

  setAccessToken(token: string) {
    this.accessTokenSubject.next(token);
  }

}

