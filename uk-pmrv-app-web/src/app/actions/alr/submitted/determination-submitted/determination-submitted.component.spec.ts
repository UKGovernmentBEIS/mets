import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrSubmittedRequestActionPayload } from '@actions/alr/testing/mock-alr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { ALRApplicationProceededToAuthorityRequestActionPayload } from 'pmrv-api';

import { AlrDeterminationSubmittedComponent } from './determination-submitted.component';

describe('DeterminationSubmittedComponent', () => {
  let component: AlrDeterminationSubmittedComponent;
  let fixture: ComponentFixture<AlrDeterminationSubmittedComponent>;
  let store: CommonActionsStore;
  let page: Page;

  class Page extends BasePage<AlrDeterminationSubmittedComponent> {
    get values() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrDeterminationSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'ALR_APPLICATION_PROCEEDED_TO_AUTHORITY',
        submitter: '123',
        payload: {
          ...alrSubmittedRequestActionPayload,
          regulatorReviewOutcome: {
            determination: {
              type: 'CLOSED_ALR',
              reason: 'A comment',
              alrFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
              files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c4'],
            },
          },
          regulatorReviewAttachments: {
            'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'testFile1.txt',
            'ebff80af-8c13-4f5a-b1eb-75b74a2121c4': 'testFile2.txt',
          },
        } as unknown as ALRApplicationProceededToAuthorityRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(AlrDeterminationSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display data', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual([
      'Close task',
      'A comment',
      'testFile1.txt',
      'testFile2.txt',
    ]);
  });
});
