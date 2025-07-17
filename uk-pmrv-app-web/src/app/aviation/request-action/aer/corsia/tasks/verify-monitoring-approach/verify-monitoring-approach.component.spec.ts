import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import VerifyMonitoringApproachComponent from '@aviation/request-action/aer/corsia/tasks/verify-monitoring-approach/verify-monitoring-approach.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerCorsiaRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<VerifyMonitoringApproachComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('VerifyMonitoringApproachComponent', () => {
  let component: VerifyMonitoringApproachComponent;
  let fixture: ComponentFixture<VerifyMonitoringApproachComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, VerifyMonitoringApproachComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          verificationReport: {
            opinionStatement: {
              fuelTypes: ['JET_KEROSENE', 'JET_GASOLINE'],
              monitoringApproachType: 'CERT_MONITORING',
              emissionsCorrect: true,
            },
          },
          totalEmissionsProvided: '1200',
          totalOffsetEmissionsProvided: '1000',
        } as AerCorsiaRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(VerifyMonitoringApproachComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Monitoring approach and emissions');
    expect(page.summaryValues).toEqual([
      [
        'Standard fuels and emission factors',
        `Jet kerosene (Jet A1 or Jet A) at 3.16 tCO2 per tonne of fuelJet gasoline (Jet B) at 3.10 tCO2 per tonne of fuel`,
      ],
      ['Monitoring approach', 'CERT only'],
      ['Emissions from all flights', '1200 tCO2'],
      ['Emissions from offset flights', '1000 tCO2'],
      ['Are the reported emissions correct?', 'Yes'],
    ]);
  });
});
