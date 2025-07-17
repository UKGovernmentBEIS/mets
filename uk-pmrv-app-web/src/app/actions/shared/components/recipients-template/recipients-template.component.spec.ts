import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { BasePage } from '@testing';

import { PermanentCessationApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { RecipientsTemplateComponent } from './recipients-template.component';

describe('RecipientsTemplateComponent', () => {
  let component: RecipientsTemplateComponent;
  let fixture: ComponentFixture<RecipientsTemplateComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<RecipientsTemplateComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h2').textContent.trim();
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element?.textContent.trim() ?? ''));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipientsTemplateComponent],
      providers: [UserInfoResolverPipe, provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        id: 102,
        type: 'PERMANENT_CESSATION_APPLICATION_SUBMITTED',
        payload: {
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

    fixture = TestBed.createComponent(RecipientsTemplateComponent);
    component = fixture.componentInstance;
    component.header = 'Recipients';
    component.officialNoticeText = 'Notice document';
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should recipients details', () => {
    expect(page.heading).toEqual('Recipients');
    expect(page.summaryListValues).toEqual([
      ['Users', 'instoper7 aaaaa, Operator admin - Financial contact, Primary contact, Service contact'],
      ['Name and signature on the notice document', 'Regulator England'],
      ['Notice document', 'letter-preview.pdf'],
    ]);
  });
});
