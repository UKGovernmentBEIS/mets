import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload } from 'pmrv-api';

import { NotificationCessationCompletedActionComponent } from './cessation-completed.component';

describe('CessationCompletedComponent', () => {
  let component: NotificationCessationCompletedActionComponent;
  let fixture: ComponentFixture<NotificationCessationCompletedActionComponent>;
  let store: CommonActionsStore;
  let page: Page;

  class Page extends BasePage<NotificationCessationCompletedActionComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationCessationCompletedActionComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        id: 1,
        type: 'PERMIT_NOTIFICATION_APPLICATION_CESSATION_COMPLETED',
        creationDate: '2022-07-27T17:11:29.82608Z',
        payload: {
          payloadType: 'PERMIT_NOTIFICATION_APPLICATION_CESSATION_COMPLETED_PAYLOAD',
          reviewDecision: {
            type: 'CESSATION_TREATED_AS_PERMANENT',
            details: {
              notes: 'Review decision details notes',
              officialNotice: 'Official notice text',
              followUp: {
                followUpResponseRequired: true,
                followUpRequest: 'Follow up request text',
                followUpResponseExpirationDate: '2025-12-11',
              },
            },
          },
          reviewDecisionNotification: {
            signatory: 'ce447c34-19a7-4310-84c6-a2931f3ab9fd',
          },
          usersInfo: {
            'c7275299-f685-46c7-b4ae-bc2ad935a64e': {
              name: 'instoper5 aaaaa',
              roleCode: 'operator_admin',
              contactTypes: ['PRIMARY', 'SERVICE', 'FINANCIAL'],
            },
            'ce447c34-19a7-4310-84c6-a2931f3ab9fd': {
              name: 'Regulator England',
            },
          },
          officialNotice: {
            name: 'Permit Notification Acknowledgement Letter.pdf',
            uuid: 'f4a5957a-09f9-4609-a8b6-fa4281585be9',
          },
          permitNotification: {
            type: 'CESSATION',
            description: 'Permit notification description',
            documents: ['11f42612-8e5a-49c4-a463-69758a5741cd', '8825987b-d11d-4138-97b9-60c39e511225'],
            startDateOfNonCompliance: '2024-11-11',
            endDateOfNonCompliance: '2025-11-11',
            isTemporary: true,
            technicalCapabilityDetails: {
              technicalCapability: 'RESUME_REG_ACTIVITIES_WITHOUT_PHYSICAL_CHANGES',
              details: 'Tech capability details',
            },
          },
          permitNotificationAttachments: {
            '11f42612-8e5a-49c4-a463-69758a5741cd': 'test1.txt',
            '8825987b-d11d-4138-97b9-60c39e511225': 'test 2.csv',
          },
        } as PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(NotificationCessationCompletedActionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the details', () => {
    expect(page.summaryListValues).toEqual([
      ['Type of change', 'Cessation of all regulated activities (temporary or permanent)'],
      ['Describe the cessation of regulated activities', 'Permit notification description'],
      ['Supporting documents', 'test1.txt  test 2.csv'],
      ['Date of cessation', '11 Nov 2024'],
      ['Do you intend to resume one or more of the regulated activities?', 'Yes'],
      ['Expected date to resume regulated activities', '11 Nov 2025'],
      [
        'Technical capability to resume regulated activities',
        'The installation is technically capable of resuming regulated activities without physical changes being made Tech capability details',
      ],
      ['Assessment of the cessation', 'Cessation treated as permanent'],
      ['Details to be included in the notification letter', 'Official notice text'],
      ['Do you require a response from the operator?', 'Yes'],
      ['Explain what the operator should cover in their response', 'Follow up request text'],
      ['Date response is needed', '11 Dec 2025'],
      ['Notes', 'Review decision details notes'],
      ['Users', 'instoper5 aaaaa, Operator admin - Primary contact, Service contact, Financial contact'],
      ['Name and signature on the notification letter', 'Regulator England'],
      ['Notification letter', 'Permit Notification Acknowledgement Letter.pdf'],
    ]);
  });
});
