import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import MonitoringApproachComponent from '@aviation/request-action/aer/corsia/tasks/monitoring-approach/monitoring-approach.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerCorsiaRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<MonitoringApproachComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('MonitoringApproachComponent', () => {
  let component: MonitoringApproachComponent;
  let fixture: ComponentFixture<MonitoringApproachComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, MonitoringApproachComponent],
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
          aer: {
            monitoringApproach: {
              certUsed: false,
              fuelUseMonitoringDetails: {
                fuelDensityType: 'ACTUAL_DENSITY',
                identicalToProcedure: true,
                blockHourUsed: true,
                aircraftTypeDetails: [
                  {
                    designator: 'C560',
                    subtype: 'test',
                    fuelBurnRatio: '1.2',
                  },
                  {
                    designator: 'A320',
                    subtype: 'test',
                    fuelBurnRatio: '1',
                  },
                ],
              },
            },
          },
        } as AerCorsiaRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(MonitoringApproachComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Monitoring approach');
    expect(page.summaryValues).toEqual([
      [
        'Other than for data gaps, did you use the CORSIA CO2 estimation and reporting tool (CERT) for your monitoring approach?',
        'No',
      ],
      ['Which fuel density type was used to determine fuel uplift in the reporting year?', 'Actual density'],
      [
        'Is the application of density data identical to the procedure used for operational and safety reasons in the emissions monitoring plan?',
        'Yes',
      ],
      ['Was fuel allocation by block hour used during the reporting year?', 'Yes'],
    ]);
  });
});
