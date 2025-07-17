import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { EmissionSourcesComponent } from '@aviation/request-action/emp/shared/tasks/emission-sources/emission-sources.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<EmissionSourcesComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
  get tierRows(): HTMLTableRowElement[] {
    return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
  }
}

describe('EmissionSourcesComponent', () => {
  let component: EmissionSourcesComponent;
  let fixture: ComponentFixture<EmissionSourcesComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestActionTaskComponent, EmpReviewDecisionGroupSummaryComponent, EmissionSourcesComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          emissionsMonitoringPlan: {
            emissionSources: {
              aircraftTypes: [
                {
                  subtype: 'Sub Type',
                  fuelTypes: ['AVIATION_GASOLINE', 'OTHER'],
                  isCurrentlyUsed: true,
                  aircraftTypeInfo: {
                    model: 'Model',
                    manufacturer: 'Manufacturer',
                    designatorType: 'Designator Type',
                  },
                  numberOfAircrafts: 100,
                },
              ],
              otherFuelExplanation: 'Other Fuel Explanation',
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(EmissionSourcesComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Emission sources');
    expect(page.summaryValues).toHaveLength(2);
    expect(page.summaryValues).toEqual([
      ['Aviation gasoline (AV gas)', '3.10 tCO2 per tonne of fuel'],
      ['Other fuel (not including sustainable aviation fuel)', 'Other Fuel Explanation'],
    ]);
    expect(page.tierRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      [
        'Manufacturer Model (Designator Type)',
        'Sub Type',
        '100',
        'Aviation gasoline (AV gas),  Other fuel (not including sustainable aviation fuel)',
      ],
    ]);
  });
});
