import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { PeerReviewDecisionSubmittedRequestActionPayload } from 'pmrv-api';

import { PeerReviewDecisionComponent } from './peer-review-decision.component';

describe('PeerReviewDecisionComponent', () => {
  let component: PeerReviewDecisionComponent;
  let fixture: ComponentFixture<PeerReviewDecisionComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<PeerReviewDecisionComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element?.textContent.trim() ?? ''));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PeerReviewDecisionComponent],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        id: 102,
        type: 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_ACCEPTED',
        payload: {
          decision: {
            type: 'AGREE',
            notes: 'My Notes',
          },
        } as PeerReviewDecisionSubmittedRequestActionPayload,
        requestId: 'INS00107-2021-1',
        requestType: 'INSTALLATION_AUDIT',
        requestAccountId: 11,
        competentAuthority: 'ENGLAND',
        submitter: 'Regulator1 England',
        creationDate: '2023-04-05T16:14:29.258067Z',
      },
    });

    fixture = TestBed.createComponent(PeerReviewDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show decision details', () => {
    expect(page.heading).toEqual('Peer review agreement submitted');
    expect(page.summaryListValues).toEqual([
      ['Decision', 'Agreed with the determination'],
      ['Supporting notes', 'My Notes'],
      ['Peer reviewer', 'Regulator1 England'],
    ]);
  });
});
