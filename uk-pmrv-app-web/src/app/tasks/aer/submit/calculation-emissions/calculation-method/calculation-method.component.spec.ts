import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ReportingDataService, TasksService } from 'pmrv-api';

import { AerModule } from '../../../aer.module';
import { mockAerApplyPayload, mockState } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { CalculationEmissionsModule } from '../calculation-emissions.module';
import { CalculationMethodComponent } from './calculation-method.component';

describe('CalculationMethodComponent', () => {
  let page: Page;
  let router: Router;

  let component: CalculationMethodComponent;
  let fixture: ComponentFixture<CalculationMethodComponent>;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const reportingDataService = mockClass(ReportingDataService);
  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  class Page extends BasePage<CalculationMethodComponent> {
    get typeRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="type"]');
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
    fixture = TestBed.createComponent(CalculationMethodComponent);
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
        { provide: ReportingDataService, useValue: reportingDataService },
      ],
    }).compileComponents();
  });

  describe('for adding a new source stream emission', () => {
    const parameterMonitoringTiers = (mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2 as any)
      .sourceStreamEmissions.parameterMonitoringTiers;

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
      reportingDataService.getInventoryDataExistenceByYear.mockReturnValueOnce(of({ exist: true }));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['You must choose a data calculation method']);

      page.submitButton.click();
      fixture.detectChanges();

      page.typeRadios[0].click();

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
                      calculationActivityDataCalculationMethod: undefined,
                      type: 'REGIONAL_DATA',
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

      expect(navigateSpy).toHaveBeenCalledWith(['../installation-postcode'], { relativeTo: route });
    });
  });

  describe('for editing a source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.typeRadios.length).toEqual(3);
      expect(page.typeRadios[0].checked).toBeFalsy();
      expect(page.typeRadios[1].checked).toBeFalsy();
      expect(page.typeRadios[2].checked).toBeTruthy();
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      reportingDataService.getInventoryDataExistenceByYear.mockReturnValueOnce(of({ exist: true }));

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
