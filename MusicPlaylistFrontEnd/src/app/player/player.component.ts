import { Component, EventEmitter, HostListener, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import { faAngleLeft, faAngleRight, faPause, faPlay, faRandom, faRecordVinyl, faRecycle, faSync, faVolumeDown, faVolumeMute, faVolumeOff, faVolumeUp } from '@fortawesome/free-solid-svg-icons';
import { PlayerService } from './player.service';

@Component({
  selector: 'app-player',
  templateUrl: './player.component.html',
  styleUrls: ['./player.component.css']
})
export class PlayerComponent implements OnInit, OnChanges {
  // FontAwesome icons
  faPlay = faPlay;
  faPause = faPause;
  faAngleRight = faAngleRight;
  faAngleLeft = faAngleLeft;
  faRandom = faRandom;
  faSync = faSync;
  faRecycle = faRecycle;
  faRecord = faRecordVinyl;

  faVolumeUp = faVolumeUp;
  faVolumeOff = faVolumeOff;
  faVolumeMute = faVolumeMute;
  faVolumeDown = faVolumeDown;

  @Input() stationNum: number = 0;
  @Input() currentStation: any = {};
  @Input() playingPlaylist: any = {};

  // Setting bars
  @Input() showPlaylistBar: boolean = false;
  @Input() showStationBar: boolean = false;
  @Input() showControls: boolean = false;

  // Spotify data
  @Input() user: any = {};
  @Input() isPlaying: boolean = false;
  @Input() currentDevice: any = {};
  @Input() currentlyPlaying: any = {};
  @Input() position: number = 0;
  @Input() isLoved: boolean = false;

  @Input() volume: number = 100;
  @Output() volumeEvent = new EventEmitter<number>();

  @Input() shuffle: boolean = false;
  @Input() repeat: number = 0;

  @Output() toggleBarEvent = new EventEmitter<number>();
  @Output() isPlayingEvent = new EventEmitter<boolean>();

  @Input() isLoading: boolean = true;
  @Output() toggleLoadingEvent = new EventEmitter<boolean>();

  @Output() setPlayerEvent = new EventEmitter<boolean>();
  @Input() isMobile: boolean = false;


  constructor(private playerService: PlayerService) {
  }
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }
  ngOnChanges(changes: SimpleChanges): void {
    throw new Error('Method not implemented.');
  }

  trackWidth: number = 0;
  divWidth: number = 0;

  // This will get the current player and set the data to the UI
  setPlayerData(): void {
   
  }

  // Trigger an event to open the playlist / station bars
  toggleBar(bar: number) {
    this.toggleBarEvent.emit(bar);
  }

  // Event for clicking play button, toggles play
  togglePlay() {
    if(!this.isLoading) {
      if(this.isPlaying) {
        // The player is playing, so we want to pause it!
        this.playerService.pause().subscribe();
      }
      else {
        // Play the player if it's on 000
        // Sync play it if it's on a playing station
        if(this.stationNum > 0 && this.currentStation) {
          // Start loading and change volume to 0
          this.toggleLoadingEvent.emit(true);
          let currentVol = this.volume;
          this.playerService.volume(0).subscribe(data => {
            this.volume = 0;
          });
            this.playerService.volume(currentVol).subscribe(data => {
              this.volume = currentVol;
              this.toggleLoadingEvent.emit(false);
              this.setPlayerEvent.emit(true);
            });
        }
        else {
          this.playerService.play().subscribe();
        }
      }
    }
  }

  // Event for right arrow, skip to the next track
  skip() {
    if(!this.isLoading) {
      // this.toggleLoadingEvent.emit(true);
      this.playerService.next().subscribe();
    }
  }

  // Event for left arrow, go to previous track
  back() {
    if(!this.isLoading) {
      // this.toggleLoadingEvent.emit(true);
      this.playerService.previous().subscribe();
    }
  }

  // Event for shuffle button, toggle shuffle
  shuffleChange(): void {
    if(!this.isLoading) {
      this.playerService.shuffle(!this.shuffle).subscribe();
    }
  }

  // Event for repeat button, toggle repeat
  repeatChange(): void {
    if(!this.isLoading) {
      switch(this.repeat) {
        case 0:
          this.playerService.repeat("context").subscribe();
          break;
        case 1:
          this.playerService.repeat("track").subscribe();
          break;
        case 2:
          this.playerService.repeat("off").subscribe();
          break;
      }
    }
  }

  // Event for volume slider, change the volume
  changeVolume(): void {
    this.volumeEvent.emit(this.volume);
  }

  // Event for clicking volume icon, toggle mute
  toggleMute(): void {
    if(this.volume > 0) {
      this.volume = 0;
    }
    else {
      this.volume = 100;
    }
    this.volumeEvent.emit(this.volume);
  }

  /**
   * Event handler for heart button. Loves or unloves a track (saves to library)
   */


  // Listens for keyboard events to control the player
  @HostListener('window:keyup', ['$event'])
  keyEvent(event: KeyboardEvent) {
    // Only work if neither the control or station panel are up and it's not loading
    if(!(this.showControls || this.showStationBar) && !this.isLoading) {
      switch(event.code) {
        // Spacebar togglePlay
        case "Space":
          this.togglePlay();
          break;
        // Left arrow back()
        case "ArrowLeft":
          this.back();
          break;
        // Right arrow skip()
        case "ArrowRight":
          this.skip();
          break;
        // S key toggleBar(1)
        case "KeyS":
          this.toggleBar(1);
          break;
        // P key toggleBar(0)
        case "KeyP":
          this.toggleBar(0);
          break;
      }
    }
  }
}