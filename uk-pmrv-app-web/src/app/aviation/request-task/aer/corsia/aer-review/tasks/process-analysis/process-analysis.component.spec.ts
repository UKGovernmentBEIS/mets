import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import { AviationAerCorsiaApplicationReviewRequestTaskPayload, AviationAerCorsiaVerificationReport } from 'pmrv-api';

import { ProcessAnalysisComponent } from './process-analysis.component';

describe('ProcessAnalysisComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: ProcessAnalysisComponent;
  let fixture: ComponentFixture<ProcessAnalysisComponent>;

  class Page extends BasePage<ProcessAnalysisComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcessAnalysisComponent, RouterTestingModule],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);

    store.setState({
      requestTaskItem: {
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            verificationReport: {
              processAnalysis: {
                verificationActivities: 'My verification activities',
                strategicAnalysis: 'My strategic analysis',
                dataSampling: 'My data sampling',
                dataSamplingResults: 'My data sampling results',
                emissionsMonitoringPlanCompliance: 'My emissions monitoring plan compliance',
              },
            } as AviationAerCorsiaVerificationReport,
          } as AviationAerCorsiaApplicationReviewRequestTaskPayload,
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(ProcessAnalysisComponent);
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
      [
        'Describe the verification activities carried out and their corresponding results',
        'My verification activities',
      ],
      ['Strategic analysis and risk assessment', 'My strategic analysis'],
      ['Data sampling', 'My data sampling'],
      ['Results of data sampling', 'My data sampling results'],
      ['Compliance with the emissions monitoring plan', 'My emissions monitoring plan compliance'],
    ]);
  });
});
