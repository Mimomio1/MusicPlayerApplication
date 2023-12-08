import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppComponent } from '../app.component';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private httpCli:HttpClient) { }

  getPlayer(): Observable<any>{
    const url = `${this.baseUrl}/getPlayer`;
    return this.httpCli.get<any>(url);
  }

  getCurrentlyPlaying(): Observable<any> {
    const url = `${this.baseUrl}/getPlayer`;
    return this.httpCli.get<any>(url);
  }

  getCurrentDevice(): Observable<any>{
    return this.httpCli.get<any>(this.baseUrl + "/getCurrentDevice");
  }

  setAMRadio(): Observable<any>{
    return this.httpCli.get<any>(`${this.baseUrl}/setAMRadio`);
  }

  getAudioFeatures(trackID: any): Observable<any>{
    return this.httpCli.get<any>(this.baseUrl + `/getAudioFeatures?trackID=${trackID}`);
  }

  getAudioAnalysis(trackID: any): Observable<any>{
    return this.httpCli.get<any>(this.baseUrl + `/getAudioAnalysis?trackID=${trackID}`);
  }

  startAMRadio(): Observable<any>{
    return this.httpCli.get<any>(this.baseUrl + `/startAMRadio`);
  }

  playTrack(uri: string): Observable<any>{
    let params = new HttpParams();
    params = params.append("trackURI", uri);
    return this.httpCli.put<any>(this.baseUrl + `/playTrack`, uri, {params: params});
  }

  playPlaylist(contextURI: string): Observable<any> {
    let params = new HttpParams();
    params = params.append("contextURI", contextURI);
    return this.httpCli.put<any>(this.baseUrl + `/playPlaylist`, contextURI, {params: params});
  }

  addToQueue(uri: string): Observable<any> {
    let params = new HttpParams();
    params = params.append("trackURI", uri);
    return this.httpCli.put<any>(this.baseUrl + `/addToQueue`, uri, {params: params});
  }

  next(): Observable<any>{
    // The api doesn't need any body data... I don't think...?
    return this.httpCli.post<any>(`${this.baseUrl}/next`, "yeet");
  }

  previous(): Observable<any> {
    // The api doesn't need any body data... I don't think...?
    return this.httpCli.post<any>(`${this.baseUrl}/previous`, "yeet");
  }

  play(): Observable<any>{
    return this.httpCli.put<any>(`${this.baseUrl}/play`, "yeet");
  }

  pause(): Observable<any>{
    return this.httpCli.put<any>(`${this.baseUrl}/pause`, "yeet");
  }

  seek(time: number): Observable<any>{
    return this.httpCli.put<any>(`${this.baseUrl}/seek?time=${time}`, time);
  }

  shuffle(activate: boolean): Observable<any>{
    return this.httpCli.put<any>(`${this.baseUrl}/shuffle?activate=${activate}`, activate);
  }

  repeat(type: string): Observable<any> {
    return this.httpCli.put<any>(`${this.baseUrl}/repeat?type=${type}`, type);
  }

  playOn(deviceID: string): Observable<any>{
    return this.httpCli.put<any>(`${this.baseUrl}/playOn?deviceID=${deviceID}`, deviceID);
  }

  volume(percent: number): Observable<any>{
    return this.httpCli.put<any>(`${this.baseUrl}/volume?percent=${percent}`, percent);
  }
}