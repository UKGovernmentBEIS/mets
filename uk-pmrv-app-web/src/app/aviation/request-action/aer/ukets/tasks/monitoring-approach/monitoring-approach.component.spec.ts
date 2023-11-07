import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import MonitoringApproachComponent from '@aviation/request-action/aer/ukets/tasks/monitoring-approach/monitoring-approach.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { AviationAerEmissionsMonitoringApproach } from 'pmrv-api';

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
        type: 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          aer: {
            monitoringApproach: {
              monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY',
              totalEmissionsType: 'FULL_SCOPE_FLIGHTS',
              fullScopeTotalEmissions: '1',
            } as AviationAerEmissionsMonitoringApproach,
          },
        } as AerRequestActionPayload,
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
      ['Monitoring approach', 'Unmodified Eurocontrol Support Facility data'],
      ['Total emissions from full-scope flights', '1 tonnes CO2'],
    ]);
  });
});
