import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';

import { alrCompletedRequestActionPayload } from '../testing/mock-alr-submitted';
import { AlrActionCompletedComponent } from './completed.component';

describe('CompletedComponent', () => {
  let component: AlrActionCompletedComponent;
  let fixture: ComponentFixture<AlrActionCompletedComponent>;
  let store: CommonActionsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrActionCompletedComponent],
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

    fixture = TestBed.createComponent(AlrActionCompletedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
