import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { PeerReviewDecisionSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';
import { DoalActionModule } from '../doal-action.module';
import { PeerReviewDecisionComponent } from './peer-review-decision.component';

describe('PeerReviewDecisionComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: PeerReviewDecisionComponent;
  let fixture: ComponentFixture<PeerReviewDecisionComponent>;

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
      imports: [DoalActionModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        id: 102,
        type: 'DOAL_APPLICATION_PEER_REVIEWER_ACCEPTED',
        payload: {
          decision: {
            type: 'AGREE',
            notes: 'My Notes',
          },
        } as PeerReviewDecisionSubmittedRequestActionPayload,
        requestId: 'DOAL00011-2021-1',
        requestType: 'DOAL',
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
    jest.clearAllMocks();
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
