import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { BasePage } from '@testing';

import { AER_CORSIA_ANNUAL_OFFSETTING, annualOffsettingMockBuild } from '../../mocks/mock-annual-offsetting';
import { sectionName } from '../../util/annual-offsetting.util';
import { AnnualOffsettingRequirementsSummaryComponent } from './offsetting-requirements-summary.component';

describe('OffsettingRequirementsSummaryComponent', () => {
  let component: AnnualOffsettingRequirementsSummaryComponent;
  let fixture: ComponentFixture<AnnualOffsettingRequirementsSummaryComponent>;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let page: Page;

  class Page extends BasePage<AnnualOffsettingRequirementsSummaryComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnualOffsettingRequirementsSummaryComponent, RouterTestingModule],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      annualOffsettingMockBuild({
        aviationAerCorsiaAnnualOffsetting: AER_CORSIA_ANNUAL_OFFSETTING,
        aviationAerCorsiaAnnualOffsettingSectionsCompleted: { [sectionName]: false },
      }) as any,
    );

    fixture = TestBed.createComponent(AnnualOffsettingRequirementsSummaryComponent);
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
    expect(page.summaryValues).toHaveLength(4);
    expect(page.summaryValues).toEqual([
      ['Scheme year', '2023'],
      ['Total Chapter 3 State Emissions (tCO2)', '333'],
      ['Sector Growth Value', '2.98%'],
      ['Calculated Annual Offsetting Requirements', '992'],
    ]);
  });

  it('should submit and navigate to task list', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveOffsettingRequirement = jest
      .spyOn(store.aerCorsiaAnnualOffsetting, 'saveOffsettingRequirement')
      .mockReturnValue(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(saveOffsettingRequirement).toHaveBeenCalledTimes(1);
    expect(saveOffsettingRequirement).toHaveBeenCalledWith(AER_CORSIA_ANNUAL_OFFSETTING);

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
