import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginsComponent } from './logins.component';

describe('LoginsComponent', () => {
  let component: LoginsComponent;
  let fixture: ComponentFixture<LoginsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LoginsComponent]
    });
    fixture = TestBed.createComponent(LoginsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
