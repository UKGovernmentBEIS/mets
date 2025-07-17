import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { PermanentCessationApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { PermanentCessationActionSubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: PermanentCessationActionSubmittedComponent;
  let fixture: ComponentFixture<PermanentCessationActionSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<PermanentCessationActionSubmittedComponent> {
    get summaryTemplate() {
      return this.query('app-permanent-cessation-details-summary-template');
    }

    get recipientsTemplate() {
      return this.query('app-action-recipients-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermanentCessationActionSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        id: 102,
        type: 'PERMANENT_CESSATION_APPLICATION_SUBMITTED',
        payload: {
          permanentCessation: {
            additionalDetails: 'Additional details',
            cessationDate: '2025-01-11',
            cessationScope: 'WHOLE_INSTALLATION',
            description: 'Description',
          },
          decisionNotification: { signatory: 'ce447c34-19a7-4310-84c6-a2931f3ab9fd' },
          officialNotice: {
            name: 'letter-preview.pdf',
            uuid: 'a918c644-eb20-453b-abed-5a555bcfe996',
          },
          usersInfo: {
            'ce447c34-19a7-4310-84c6-a2931f3ab9fd': { name: 'Regulator England' },
            '5c272217-1b33-42ec-9354-c8ea907c7033': {
              name: 'instoper7 aaaaa',
              roleCode: 'operator_admin',
              contactTypes: ['FINANCIAL', 'PRIMARY', 'SERVICE'],
            },
          },
        } as PermanentCessationApplicationSubmittedRequestActionPayload,
        requestId: 'INS00107-2021-1',
        requestType: 'PERMANENT_CESSATION',
        requestAccountId: 11,
        competentAuthority: 'ENGLAND',
        submitter: 'Regulator1 England',
        creationDate: '2023-04-05T16:14:29.258067Z',
      },
    });

    fixture = TestBed.createComponent(PermanentCessationActionSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.summaryTemplate).toBeTruthy();
    expect(page.recipientsTemplate).toBeTruthy();
  });
});
