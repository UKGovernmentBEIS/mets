import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { MeasurementReviewComponent } from './measurement-review.component';

describe('MeasurementReviewComponent', () => {
  let component: MeasurementReviewComponent;
  let fixture: ComponentFixture<MeasurementReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MEASUREMENT_CO2',
      taskKey: 'MEASUREMENT_CO2',
    },
  );

  class Page extends BasePage<MeasurementReviewComponent> {
    get rows() {
      return this.queryAll<HTMLTableRowElement>('govuk-table tr')
        .filter((el) => !el.querySelector('th'))
        .map((el) => Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
    get decisionSummaryListValues() {
      return this.queryAll<HTMLDivElement>('app-aer-review-group-decision .govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(MeasurementReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Measurement of CO2 emissions');
    expect(page.rows).toEqual([
      ['EP1', 'the reference Anthracite', 'emission source 1 reference', '-37.026', '0', ''],
      ['Total emissions', '', '', '-37.026 tCO2e', '0 tCO2e', ''],
    ]);
  });

  it('should show the decision summary', () => {
    expect(page.decisionSummaryListValues).toEqual([
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);
  });
});
