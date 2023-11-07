import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { AerModule } from '../../../aer.module';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { CalculationEmissionsModule } from '../calculation-emissions.module';
import { NationalInventoryService } from '../services/national-inventory.service';
import { RelevantCategoryComponent } from './relevant-category.component';

describe('RelevantCategoryComponent', () => {
  let page: Page;
  let router: Router;

  let component: RelevantCategoryComponent;
  let fixture: ComponentFixture<RelevantCategoryComponent>;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  const parameterMonitoringTiers = (mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2 as any)
    .sourceStreamEmissions[0].parameterMonitoringTiers;

  const mockSectorName = '1A1a';
  const mockFuelName = 'Coal';
  const nationalInventoryService = {
    nationalInventoryData$: of({
      sectors: [
        {
          name: mockSectorName,
          displayName: 'Public Electricity & Heat Production',
          fuels: [
            {
              name: mockFuelName,
            },
          ],
        },
      ],
    }),
  };

  class Page extends BasePage<RelevantCategoryComponent> {
    get sectors() {
      return this.queryAll<HTMLInputElement>('input[name$="sector"]');
    }

    get firstSectorFuels() {
      const firstSectorValue = this.sectors[0].value;
      return this.queryAll<HTMLInputElement>(`input[name$="sector_${firstSectorValue}"]`);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }
  const createComponent = () => {
    fixture = TestBed.createComponent(RelevantCategoryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, CalculationEmissionsModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: NationalInventoryService, useValue: nationalInventoryService },
      ],
    }).compileComponents();
  });

  describe('for adding a new source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [
                  {
                    parameterCalculationMethod: {
                      type: 'NATIONAL_INVENTORY_DATA',
                    },
                    parameterMonitoringTiers,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select an option']);

      page.submitButton.click();
      fixture.detectChanges();

      page.sectors[0].click();
      fixture.detectChanges();

      page.firstSectorFuels[0].click();
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [
                  {
                    parameterCalculationMethod: {
                      fuel: mockFuelName,
                      mainActivitySector: mockSectorName,
                      type: 'NATIONAL_INVENTORY_DATA',
                    },
                    parameterMonitoringTiers,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../activity-calculation-method'], { relativeTo: route });
    });
  });

  describe('for editing a source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [
                  {
                    parameterCalculationMethod: {
                      fuel: mockFuelName,
                      mainActivitySector: mockSectorName,
                      type: 'NATIONAL_INVENTORY_DATA',
                    },
                    parameterMonitoringTiers,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.sectors.length).toEqual(1);
      expect(page.firstSectorFuels.length).toEqual(1);
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../activity-calculation-method'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
