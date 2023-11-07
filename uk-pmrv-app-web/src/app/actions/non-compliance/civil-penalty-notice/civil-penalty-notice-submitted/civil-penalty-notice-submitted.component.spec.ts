import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../../../shared/action-shared-module';
import { CivilPenaltyNoticeSubmittedComponent } from './civil-penalty-notice-submitted.component';

describe('CivilPenaltyNoticeSubmittedComponent', () => {
  let component: CivilPenaltyNoticeSubmittedComponent;
  let fixture: ComponentFixture<CivilPenaltyNoticeSubmittedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CivilPenaltyNoticeSubmittedComponent],
      imports: [ActionSharedModule, SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CivilPenaltyNoticeSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
