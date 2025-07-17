import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import {
  AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING,
  threeYearPeriodlOffsettingMockBuild,
} from '../../mocks/mock-3year-period-offsetting';
import { ThreeYearOffsettingRequirementsSummaryComponent } from './offsetting-requirements-summary.component';

describe('OffsettingRequirementsSummaryComponent', () => {
  let component: ThreeYearOffsettingRequirementsSummaryComponent;
  let fixture: ComponentFixture<ThreeYearOffsettingRequirementsSummaryComponent>;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let page: Page;

  class Page extends BasePage<ThreeYearOffsettingRequirementsSummaryComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get govukTable() {
      return this.query('app-3year-offsetting-requirements-table-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThreeYearOffsettingRequirementsSummaryComponent, RouterTestingModule],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      threeYearPeriodlOffsettingMockBuild({
        aviationAerCorsia3YearPeriodOffsetting: AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING,
      }),
    );

    fixture = TestBed.createComponent(ThreeYearOffsettingRequirementsSummaryComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', async () => {
    expect(page.govukTable).toBeTruthy();
    expect(page.summaryValues).toHaveLength(1);
    expect(page.summaryValues).toEqual([
      ['Does the operator have any offsetting requirements for this period?', 'Yes'],
    ]);
  });

  it('should submit and navigate to task list', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveOffsettingRequirement = jest
      .spyOn(store.aerCorsia3YearPeriodOffsetting, 'saveOffsettingRequirement')
      .mockReturnValue(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(saveOffsettingRequirement).toHaveBeenCalledTimes(1);
    expect(saveOffsettingRequirement).toHaveBeenCalledWith(AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING);

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
