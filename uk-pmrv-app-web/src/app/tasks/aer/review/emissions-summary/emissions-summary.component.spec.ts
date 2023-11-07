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

import { EmissionsSummaryComponent } from './emissions-summary.component';

describe('EmissionsSummaryComponent', () => {
  let component: EmissionsSummaryComponent;
  let fixture: ComponentFixture<EmissionsSummaryComponent>;

  let page: Page;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'FUELS_AND_EQUIPMENT',
    },
  );

  class Page extends BasePage<EmissionsSummaryComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get emissionsCalculations() {
      return this.queryAll<HTMLDListElement>('tr');
    }

    get emissionsCalculationsTextContents() {
      return this.emissionsCalculations.map((sourceStream) =>
        Array.from(sourceStream.querySelectorAll('td')).map((td) => td.textContent.trim()),
      );
    }
    get decisionSummaryListValues() {
      return this.queryAll<HTMLDivElement>('app-aer-review-group-decision .govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(EmissionsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Emissions summary');
  });

  it('should show the decision summary', () => {
    expect(page.decisionSummaryListValues).toEqual([
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);
  });
});
