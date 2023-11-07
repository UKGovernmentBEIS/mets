import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { ApproachesTierReviewSummaryComponent } from '@tasks/aer/shared/components/approaches-tier-review-summary/approaches-tier-review-summary.component';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('ApproachesTierReviewSummaryComponent', () => {
  let component: ApproachesTierReviewSummaryComponent;
  let fixture: ComponentFixture<ApproachesTierReviewSummaryComponent>;
  let store: CommonTasksStore;
  let page: Page;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'CALCULATION_CO2',
    },
  );

  class Page extends BasePage<ApproachesTierReviewSummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get heading() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get headings() {
      return this.queryAll<HTMLHeadingElement>('h2').map((el) => el.textContent.trim());
    }
  }

  const createComponent = () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ApproachesTierReviewSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [SharedModule, AerModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => createComponent());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary details', () => {
    expect(page.heading).toEqual('the reference Anthracite');

    expect(page.headings).toEqual([
      'Emission network',
      'Transferred CO2',
      'Parameter values',
      'Reason for not using monitoring plan tiers',
      'Emission factor',
      'Activity data',
      'Net calorific value',
      'Oxidation factor',
      'Calculated emissions',
    ]);
  });
});
