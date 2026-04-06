import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { nerSubmittedRequestActionPayload } from '@actions/ner/testing';
import { CommonActionsStore } from '@actions/store/common-actions.store';

import { NerActionDetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: NerActionDetailsComponent;
  let fixture: ComponentFixture<NerActionDetailsComponent>;
  let store: CommonActionsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerActionDetailsComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'NER_APPLICATION_SENT_TO_VERIFIER',
        submitter: '123',
        payload: nerSubmittedRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(NerActionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
