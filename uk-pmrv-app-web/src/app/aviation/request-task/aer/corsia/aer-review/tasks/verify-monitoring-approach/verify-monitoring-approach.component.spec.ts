import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import { AviationAerCorsiaApplicationReviewRequestTaskPayload, AviationAerCorsiaVerificationReport } from 'pmrv-api';

import { VerifyMonitoringApproachComponent } from './verify-monitoring-approach.component';

describe('VerifyMonitoringApproachComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: VerifyMonitoringApproachComponent;
  let fixture: ComponentFixture<VerifyMonitoringApproachComponent>;

  class Page extends BasePage<VerifyMonitoringApproachComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifyMonitoringApproachComponent, RouterTestingModule],
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
              opinionStatement: {
                fuelTypes: ['JET_KEROSENE', 'JET_GASOLINE'],
                monitoringApproachType: 'CERT_MONITORING',
                emissionsCorrect: true,
              },
            } as AviationAerCorsiaVerificationReport,
            totalEmissionsProvided: '1200',
            totalOffsetEmissionsProvided: '1000',
          } as AviationAerCorsiaApplicationReviewRequestTaskPayload,
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(VerifyMonitoringApproachComponent);
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
        'Standard fuels and emission factors',
        'Jet kerosene (Jet A1 or Jet A) at 3.16 tCO2 per tonne of fuelJet gasoline (Jet B) at 3.10 tCO2 per tonne of fuel',
      ],
      ['Monitoring approach', 'CERT only'],
      ['Emissions from all flights', '1200 tCO2'],
      ['Emissions from offset flights', '1000 tCO2'],
      ['Are the reported emissions correct?', 'Yes'],
    ]);
  });
});
