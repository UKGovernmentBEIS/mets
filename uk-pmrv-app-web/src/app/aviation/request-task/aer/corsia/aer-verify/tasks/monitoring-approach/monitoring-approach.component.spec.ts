import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringApproachComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/monitoring-approach/monitoring-approach.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

describe('MonitoringApproachComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: MonitoringApproachComponent;
  let fixture: ComponentFixture<MonitoringApproachComponent>;

  class Page extends BasePage<MonitoringApproachComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitoringApproachComponent, RouterTestingModule],
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
          },
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(MonitoringApproachComponent);
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
