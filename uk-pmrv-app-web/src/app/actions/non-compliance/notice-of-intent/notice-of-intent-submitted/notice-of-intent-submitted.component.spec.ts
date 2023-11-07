import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../../../shared/action-shared-module';
import { NoticeOfIntentSubmittedComponent } from './notice-of-intent-submitted.component';

describe('NoticeOfIntentSubmittedComponent', () => {
  let component: NoticeOfIntentSubmittedComponent;
  let fixture: ComponentFixture<NoticeOfIntentSubmittedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NoticeOfIntentSubmittedComponent],
      imports: [ActionSharedModule, SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeOfIntentSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
