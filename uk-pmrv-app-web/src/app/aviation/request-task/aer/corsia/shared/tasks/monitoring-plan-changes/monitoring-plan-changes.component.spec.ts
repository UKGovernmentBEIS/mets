import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringPlanChangesComponent } from '@aviation/request-task/aer/corsia/shared/tasks/monitoring-plan-changes/monitoring-plan-changes.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

describe('MonitoringPlanChangesComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: MonitoringPlanChangesComponent;
  let fixture: ComponentFixture<MonitoringPlanChangesComponent>;

  class Page extends BasePage<MonitoringPlanChangesComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitoringPlanChangesComponent, RouterTestingModule],
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
            aerMonitoringPlanVersions: [
              {
                empId: '1UK-E-AV-11399',
                empApprovalDate: '2020-11-14',
                empConsolidationNumber: 13,
              },
            ],
            aer: {
              aerMonitoringPlanChanges: {
                notCoveredChangesExist: true,
                details: 'My details',
              },
            },
          },
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(MonitoringPlanChangesComponent);
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
      ['1UK-E-AV-11399 v13', '14 Nov 2020'],
      ['Were there any changes not covered by the emissions monitoring plans in the reporting year?', 'Yes'],
      ['Changes reported by the operator', 'My details'],
    ]);
  });
});
