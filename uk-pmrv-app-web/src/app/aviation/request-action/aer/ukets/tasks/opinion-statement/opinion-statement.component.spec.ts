import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerUkEtsRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import OpinionStatementComponent from './opinion-statement.component';

class Page extends BasePage<OpinionStatementComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('OpinionStatementComponent', () => {
  let fixture: ComponentFixture<OpinionStatementComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, OpinionStatementComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED',
        creationDate: '2023-09-20T12:18:46.714Z',
        payload: {
          aer: {
            aerMonitoringPlanChanges: {
              notCoveredChangesExist: false,
              details: 'details',
            },
          },
          verificationReport: {
            opinionStatement: {
              fuelTypes: ['JET_KEROSENE'],
              monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY',
              emissionsCorrect: true,
              manuallyProvidedEmissions: 'emissions',
              additionalChangesNotCovered: true,
              additionalChangesNotCoveredDetails: 'details',
              siteVisit: {
                type: 'IN_PERSON',
              },
            },
          },
        } as AerUkEtsRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(OpinionStatementComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Opinion statement');

    expect(page.summaryValues).toEqual([
      ['Standard fuels and emission factors', 'Jet kerosene (Jet A1 or Jet A) at 3.15 tCO2 per tonne of fuel'],
      ['Monitoring approach', 'Unmodified Eurocontrol Support Facility data'],
      ['Total emissions reported by the operator', 'tCO2'],
      ['Are the reported emissions correct?', 'Yes'],
      ['Changes reported by the operator', 'None'],
      ['Changes reported by the verifier', 'details'],
      ['What kind of site visit did your team make?', 'In person site visit'],
      ['Site visits', ''],
      ['Team members involved', ''],
    ]);
  });
});
