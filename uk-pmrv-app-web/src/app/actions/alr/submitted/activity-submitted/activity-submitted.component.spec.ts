import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrSubmittedRequestActionPayload } from '@actions/alr/testing/mock-alr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';

import { AlrActivitySubmittedComponent } from './activity-submitted.component';

describe('ActivitySubmittedComponent', () => {
  let component: AlrActivitySubmittedComponent;
  let fixture: ComponentFixture<AlrActivitySubmittedComponent>;
  let store: CommonActionsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrActivitySubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'ALR_APPLICATION_SENT_TO_VERIFIER',
        submitter: '123',
        payload: alrSubmittedRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(AlrActivitySubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
