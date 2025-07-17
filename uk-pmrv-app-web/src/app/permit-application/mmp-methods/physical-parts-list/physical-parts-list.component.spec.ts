import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { mockDigitizedPlanDetails } from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { DigitizedPlan, SubInstallation } from 'pmrv-api';

import { PhysicalPartsListComponent } from './physical-parts-list.component';

describe('PhysicalPartsListComponent', () => {
  let component: PhysicalPartsListComponent;
  let fixture: ComponentFixture<PhysicalPartsListComponent>;
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let activatedRoute: ActivatedRoute;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });

  const createComponent = (createError?: boolean) => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              methodTask: {
                physicalPartsAndUnitsAnswer: true,
                connections: createError === undefined ? undefined : mockDigitizedPlanDetails.methodTask.connections,
              },
              subInstallations: [
                ...mockDigitizedPlanDetails.subInstallations,
                {
                  subInstallationNo: '1',
                  subInstallationType: !createError ? 'ADIPIC_ACID' : 'AROMATICS',
                } as SubInstallation,
              ],
            } as DigitizedPlan,
          },
        },
        {
          MMP_SUB_INSTALLATION_Product_Benchmark: [true, true],
        },
      ),
    );

    fixture = TestBed.createComponent(PhysicalPartsListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  };

  class Page extends BasePage<PhysicalPartsListComponent> {
    get continueButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get physicalPathSummaries(): HTMLDListElement[] {
      return this.queryAll<HTMLDListElement>('tr');
    }

    get physicalPathSummariesTextContents(): string[][] {
      return this.physicalPathSummaries.map((physicalPathSummary) =>
        Array.from(physicalPathSummary.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary')?.querySelectorAll('a') ?? []).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhysicalPartsListComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should display physical path summaries', () => {
    createComponent(false);
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.physicalPathSummaries).toHaveLength(2);
    expect(page.physicalPathSummariesTextContents).toEqual([
      [],
      ['Test 1', 'Adipic acid  Ammonia', '', 'Remove', 'Change'],
    ]);

    page.continueButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../assign-parts'], { relativeTo: activatedRoute });
  });

  it('should not display physical path summaries and show error when physical parts list is empty', () => {
    createComponent();

    page.continueButton.click();
    fixture.detectChanges();

    expect(page.physicalPathSummaries).toHaveLength(0);
    expect(page.physicalPathSummariesTextContents).toEqual([]);

    expect(page.errorSummaryLinks).toEqual(['Select at least one physical part']);
  });

  it('should show errors when physical parts list is not empty', () => {
    createComponent(true);

    expect(page.physicalPathSummaries).toHaveLength(2);
    expect(page.physicalPathSummariesTextContents).toEqual([
      [],
      ['Test 1', 'Select at least two sub-installations', '', 'Remove', 'Change'],
    ]);

    page.continueButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual(['Select at least two sub-installations']);
  });
});
