import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import { AviationAerCorsiaApplicationReviewRequestTaskPayload, AviationAerCorsiaVerificationReport } from 'pmrv-api';

import { VerifiersConclusionsComponent } from './verifiers-conclusions.component';

describe('VerifiersConclusionsComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: VerifiersConclusionsComponent;
  let fixture: ComponentFixture<VerifiersConclusionsComponent>;

  class Page extends BasePage<VerifiersConclusionsComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifiersConclusionsComponent, RouterTestingModule],
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
              verifiersConclusions: {
                dataQualityMateriality: 'My quality of data',
                materialityThresholdType: 'THRESHOLD_2_PER_CENT',
                materialityThresholdMet: true,
                emissionsReportConclusion: 'My conclusion',
              },
            } as AviationAerCorsiaVerificationReport,
          } as AviationAerCorsiaApplicationReviewRequestTaskPayload,
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(VerifiersConclusionsComponent);
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
      ['Conclusions on data quality', 'My quality of data'],
      ['Materiality threshold', '2%'],
      ['Is this materiality threshold being met in the emissions report?', 'Yes'],
      ["Conclusion relating to the operator's emissions report", 'My conclusion'],
    ]);
  });
});
