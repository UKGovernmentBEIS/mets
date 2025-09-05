import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrCompletedRequestActionPayload } from '@actions/alr/testing/mock-alr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';

import { AlrActionDateSubmittedComponent } from './date-submitted.component';

describe('DateSubmittedComponent', () => {
  let component: AlrActionDateSubmittedComponent;
  let fixture: ComponentFixture<AlrActionDateSubmittedComponent>;
  let store: CommonActionsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrActionDateSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'ALR_APPLICATION_ACCEPTED_WITH_CORRECTIONS',
        submitter: '123',
        payload: alrCompletedRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(AlrActionDateSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
