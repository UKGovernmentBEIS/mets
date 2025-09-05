import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrCompletedRequestActionPayload } from '@actions/alr/testing/mock-alr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';

import { AlrActionCompletedSubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: AlrActionCompletedSubmittedComponent;
  let fixture: ComponentFixture<AlrActionCompletedSubmittedComponent>;
  let store: CommonActionsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrActionCompletedSubmittedComponent],
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

    fixture = TestBed.createComponent(AlrActionCompletedSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
