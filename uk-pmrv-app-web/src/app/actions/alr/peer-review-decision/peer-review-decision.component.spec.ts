import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { PeerReviewDecisionSubmittedRequestActionPayload } from 'pmrv-api';

import { AlrPeerReviewDecisionComponent } from './peer-review-decision.component';

describe('PeerReviewDecisionComponent', () => {
  let component: AlrPeerReviewDecisionComponent;
  let fixture: ComponentFixture<AlrPeerReviewDecisionComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<AlrPeerReviewDecisionComponent> {
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
      imports: [AlrPeerReviewDecisionComponent],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        id: 102,
        type: 'ALR_APPLICATION_PEER_REVIEW_ACCEPTED',
        payload: {
          decision: {
            type: 'AGREE',
            notes: 'My Notes',
          },
        } as PeerReviewDecisionSubmittedRequestActionPayload,
        requestId: 'ALR00107-2021-1',
        requestType: 'ALR',
        requestAccountId: 11,
        competentAuthority: 'ENGLAND',
        submitter: 'Regulator1 England',
        creationDate: '2023-04-05T16:14:29.258067Z',
      },
    });

    fixture = TestBed.createComponent(AlrPeerReviewDecisionComponent);
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
