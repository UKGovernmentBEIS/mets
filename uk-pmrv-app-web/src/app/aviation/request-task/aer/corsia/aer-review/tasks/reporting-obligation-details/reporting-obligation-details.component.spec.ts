import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import { AviationAerCorsiaApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { ReportingObligationDetailsComponent } from './reporting-obligation-details.component';

describe('ReportingObligationDetailsComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: ReportingObligationDetailsComponent;
  let fixture: ComponentFixture<ReportingObligationDetailsComponent>;

  class Page extends BasePage<ReportingObligationDetailsComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportingObligationDetailsComponent, RouterTestingModule],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);

    store.setState({
      requestTaskItem: {
        requestInfo: {
          type: 'AVIATION_AER_CORSIA',
          requestMetadata: { year: 2022 },
        },
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            reportingRequired: false,
            reportingObligationDetails: {
              noReportingReason: 'Test reason',
              supportingDocuments: ['randomUUID1'],
            },
            aerAttachments: {
              randomUUID1: 'reportingObligationFile.png',
            },
          } as AviationAerCorsiaApplicationReviewRequestTaskPayload,
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(ReportingObligationDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.summaryValues).toEqual([
      ['Are you required to complete a 2022 emissions report?', 'No'],
      ['Reasons', 'Test reason'],
      ['Supporting documents', 'reportingObligationFile.png'],
    ]);
  });
});
