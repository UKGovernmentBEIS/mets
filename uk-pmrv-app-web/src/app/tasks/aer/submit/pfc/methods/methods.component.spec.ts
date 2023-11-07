import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { ReportingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { AerModule } from '../../../aer.module';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { PfcModule } from '../pfc.module';
import { MethodsComponent } from './methods.component';

describe('MethodsComponent', () => {
  let page: Page;
  let router: Router;
  let component: MethodsComponent;
  let fixture: ComponentFixture<MethodsComponent>;

  let reportingService: ReportingService;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, { methodType: 'METHOD_A' });

  class Page extends BasePage<MethodsComponent> {
    get anodeEffectsPerCellDay() {
      return this.getInputValue('#anodeEffectsPerCellDay');
    }
    set anodeEffectsPerCellDay(value: string) {
      this.setInputValue('#anodeEffectsPerCellDay', value);
    }

    get averageDurationOfAnodeEffectsInMinutes() {
      return this.getInputValue('#averageDurationOfAnodeEffectsInMinutes');
    }
    set averageDurationOfAnodeEffectsInMinutes(value: string) {
      this.setInputValue('#averageDurationOfAnodeEffectsInMinutes', value);
    }

    get slopeCF4EmissionFactor() {
      return this.getInputValue('#slopeCF4EmissionFactor');
    }
    set slopeCF4EmissionFactor(value: string) {
      this.setInputValue('#slopeCF4EmissionFactor', value);
    }

    get c2F6WeightFraction() {
      return this.getInputValue('#c2F6WeightFraction');
    }
    set c2F6WeightFraction(value: string) {
      this.setInputValue('#c2F6WeightFraction', value);
    }

    get percentageOfCollectionEfficiency() {
      return this.getInputValue('#percentageOfCollectionEfficiency');
    }
    set percentageOfCollectionEfficiency(value: string) {
      this.setInputValue('#percentageOfCollectionEfficiency', value);
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
    fixture = TestBed.createComponent(MethodsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    reportingService = TestBed.inject(ReportingService);

    jest.spyOn(reportingService, 'calculatePfcEmissions').mockReturnValue(
      of({
        globalWarmingPotentialCF4: '1',
        globalWarmingPotentialC2F: '1',
        amountOfCF4: '1',
        totalCF4Emissions: '1',
        amountOfC2F6: '1',
        totalC2F6Emissions: '1',
        reportableEmissions: '1',
      }),
    );

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, PfcModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
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
              CALCULATION_PFC: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_PFC,
                sourceStreamEmissions: [{}],
              },
            },
          },
          {
            CALCULATION_PFC: [],
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
      expect(page.errorSummaryLinks).toEqual([
        'Enter a value',
        'Enter a value',
        'Enter a value',
        'Enter a value',
        'Enter a value',
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      page.anodeEffectsPerCellDay = '3';
      page.averageDurationOfAnodeEffectsInMinutes = '1';
      page.slopeCF4EmissionFactor = '2';
      page.c2F6WeightFraction = '2';
      page.percentageOfCollectionEfficiency = '3';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_PFC: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_PFC,
                sourceStreamEmissions: [
                  {
                    amountOfC2F6: '1',
                    amountOfCF4: '1',
                    calculationCorrect: null,
                    pfcSourceStreamEmissionCalculationMethodData: {
                      anodeEffectsPerCellDay: '3',
                      averageDurationOfAnodeEffectsInMinutes: '1',
                      c2F6WeightFraction: '2',
                      calculationMethod: undefined,
                      percentageOfCollectionEfficiency: '3',
                      slopeCF4EmissionFactor: '2',
                    },
                    reportableEmissions: '1',
                    totalC2F6Emissions: '1',
                    totalCF4Emissions: '1',
                  },
                ],
              },
            },
          },
          {
            CALCULATION_PFC: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../calculation-review'], { relativeTo: route });
    });
  });
});
