import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../../../shared/action-shared-module';
import { DailyPenaltyNoticeSubmittedComponent } from './daily-penalty-notice-submitted.component';

describe('DailyPenaltyNoticeSubmittedComponent', () => {
  let component: DailyPenaltyNoticeSubmittedComponent;
  let fixture: ComponentFixture<DailyPenaltyNoticeSubmittedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DailyPenaltyNoticeSubmittedComponent],
      imports: [ActionSharedModule, SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DailyPenaltyNoticeSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
