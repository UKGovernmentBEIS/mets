import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { RequestActionInfoDTO } from 'pmrv-api';

import { DecisionSummaryComponent } from './decision-summary.component';

describe('DecisionSummaryComponent', () => {
  let component: DecisionSummaryComponent;
  let fixture: ComponentFixture<DecisionSummaryComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();
  const govukDate = new GovukDatePipe();
  const currentDate = new Date().toISOString();
  const currentDateText = govukDate.transform(currentDate, 'datetime');
  class Page extends BasePage<DecisionSummaryComponent> {
    get heading() {
      return `${this.query<HTMLHeadingElement>(
        'app-request-action-heading h1',
      ).textContent.trim()} ${this.query<HTMLHeadingElement>('app-request-action-heading p').textContent.trim()}`;
    }
    get titles() {
      return this.queryAll<HTMLDListElement>('dl').map((el) =>
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
      );
    }
    get values() {
      return this.queryAll<HTMLDListElement>('dl').map((el) =>
        Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
    get empApplicationLink() {
      return (
        this.queryAll<HTMLAnchorElement>('dl a').find((a) => a.textContent.trim() === 'Emissions plan application') ??
        null
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionSummaryComponent, SharedModule, RequestActionTaskComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
  });

  function setup(requestActionItem: Partial<RequestActionInfoDTO> & { type: RequestActionInfoDTO['type'] }): void {
    store.setState({
      requestActionItem: { creationDate: currentDate, ...requestActionItem } as any,
      regulatorViewer: true,
    });

    fixture = TestBed.createComponent(DecisionSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  it('should create', () => {
    setup({
      type: 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED',
      payload: {
        determination: { type: 'APPROVED', reason: 'reason' },
        usersInfo: { reg: { name: 'Regulator Name' } },
        decisionNotification: { operators: [], signatory: 'reg' },
        officialNotice: { name: 'notice.pdf', uuid: 'uuid' },
      } as any,
    });

    expect(component).toBeTruthy();
  });

  it('should show the details', () => {
    setup({
      type: 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED',
      payload: {
        determination: {
          type: 'APPROVED',
          reason: 'reason',
        },
        usersInfo: {
          op1: {
            contactTypes: ['PRIMARY'],
            name: 'Operator1 Name',
          },
          op2: {
            contactTypes: ['PRIMARY'],
            name: 'Operator2 Name',
          },
          reg: {
            name: 'Regulator Name',
          },
        },
        decisionNotification: {
          operators: ['op1', 'op2'],
          signatory: 'reg',
        },
        empDocument: {
          name: 'UK-E-AV-00081 v1.pdf',
          uuid: 'cafdd4c4-a010-4251-a105-3c489cee8590',
        },
        officialNotice: {
          name: 'EMP_application_approved.pdf',
          uuid: 'b7245908-0f9c-4562-8f3a-177ef5d1503b',
        },
      } as any,
    });

    expect(page.heading).toEqual(`Approved ${currentDateText}`);
    expect(page.titles).toEqual([
      ['Emissions plan', 'Emissions plan application', 'Decision', 'Reason for decision'],
      ['Users', 'Name and signature on the official notice', 'Official notice'],
    ]);
    expect(page.values).toEqual([
      ['UK-E-AV-00081 v1.pdf', 'Emissions plan application', 'Approve', 'reason'],
      [
        'Operator1 Name - Primary contact  Operator2 Name - Primary contact',
        'Regulator Name',
        'EMP_application_approved.pdf',
      ],
    ]);
  });

  describe('emissions plan application link for rejected/withdrawn decisions (METS-2604)', () => {
    const basePayload = {
      // the EMP application snapshot - only present on decisions made after the METS-2638 payload change
      emissionsMonitoringPlan: {},
      usersInfo: {
        reg: {
          name: 'Regulator Name',
        },
      },
      decisionNotification: {
        operators: [],
        signatory: 'reg',
      },
      officialNotice: {
        name: 'EMP_application_notice.pdf',
        uuid: 'b7245908-0f9c-4562-8f3a-177ef5d1503b',
      },
    };

    it('should show the link for a deemed withdrawn EMP issuance application', () => {
      setup({
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN',
        payload: {
          ...basePayload,
          determination: { type: 'DEEMED_WITHDRAWN', reason: 'reason' },
        } as any,
      });

      expect(page.heading).toEqual(`Deemed withdrawn ${currentDateText}`);
      expect(page.titles[0]).toEqual(['Emissions plan application', 'Decision', 'Reason for decision']);
      expect(page.empApplicationLink.textContent.trim()).toEqual('Emissions plan application');
    });

    it('should show the link for a rejected EMP variation application', () => {
      setup({
        type: 'EMP_VARIATION_UKETS_APPLICATION_REJECTED',
        payload: {
          ...basePayload,
          determination: { type: 'REJECTED', reason: 'reason' },
        } as any,
      });

      expect(page.heading).toEqual(`Rejected ${currentDateText}`);
      expect(page.titles[0]).toEqual([
        'Emissions plan application',
        'Decision',
        'Text to be included in the refusal notice',
      ]);
      expect(page.empApplicationLink.textContent.trim()).toEqual('Emissions plan application');
    });

    it('should show the link for a deemed withdrawn EMP variation application', () => {
      setup({
        type: 'EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN',
        payload: {
          ...basePayload,
          determination: { type: 'DEEMED_WITHDRAWN', reason: 'reason' },
        } as any,
      });

      expect(page.heading).toEqual(`Deemed withdrawn ${currentDateText}`);
      expect(page.titles[0]).toEqual(['Emissions plan application', 'Decision', 'Reason for decision']);
      expect(page.empApplicationLink.textContent.trim()).toEqual('Emissions plan application');
    });

    // Older Withdrawn/Rejected decisions predate the EMP application snapshot on the payload. Their type
    // and decision status are unreliable and there is nothing to link to, so the "Emissions plan
    // application" link must not be rendered even though the decision itself still displays.
    it('should hide the link for a deemed withdrawn EMP issuance application with no snapshot', () => {
      setup({
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN',
        payload: {
          usersInfo: { reg: { name: 'Regulator Name' } },
          decisionNotification: { operators: [], signatory: 'reg' },
          determination: { type: 'DEEMED_WITHDRAWN', reason: 'reason' },
        } as any,
      });

      expect(page.heading).toEqual(`Deemed withdrawn ${currentDateText}`);
      expect(page.empApplicationLink).toBeNull();
    });

    it('should hide the link for a rejected EMP variation application with no snapshot', () => {
      setup({
        type: 'EMP_VARIATION_UKETS_APPLICATION_REJECTED',
        payload: {
          usersInfo: { reg: { name: 'Regulator Name' } },
          decisionNotification: { operators: [], signatory: 'reg' },
          determination: { type: 'REJECTED', reason: 'reason' },
        } as any,
      });

      expect(page.heading).toEqual(`Rejected ${currentDateText}`);
      expect(page.empApplicationLink).toBeNull();
    });

    it('should fall back to the request action type for the heading when the determination is missing', () => {
      setup({
        type: 'EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN',
        payload: {
          usersInfo: { reg: { name: 'Regulator Name' } },
          decisionNotification: { operators: [], signatory: 'reg' },
        } as any,
      });

      expect(page.heading).toEqual(`Deemed withdrawn ${currentDateText}`);
      expect(page.empApplicationLink).toBeNull();
    });
  });
});
