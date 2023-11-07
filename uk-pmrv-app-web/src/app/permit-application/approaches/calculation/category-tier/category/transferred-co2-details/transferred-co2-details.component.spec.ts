import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationModule } from '@permit-application/approaches/calculation/calculation.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { CalculationOfCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { TransferredCo2DetailsComponent } from './transferred-co2-details.component';

describe('TransferredCo2DetailsComponent', () => {
  let component: TransferredCo2DetailsComponent;
  let fixture: ComponentFixture<TransferredCo2DetailsComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let router: Router;
  let activatedRoute: ActivatedRouteSnapshotStub;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '237', index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION_CO2.sourceStreamCategoryAppliedTiers',
  });

  class Page extends BasePage<TransferredCo2DetailsComponent> {
    set emitterId(value: string) {
      this.setInputValue('#emitterId', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TransferredCo2DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' });
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: ActivatedRouteSnapshotStub, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('for editing source stream category', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(mockState, {
          CALCULATION_CO2_Category: [true],
          CALCULATION_CO2_Activity_Data: [false],
          CALCULATION_CO2_Calorific: [false],
          CALCULATION_CO2_Emission_Factor: [false],
          CALCULATION_CO2_Oxidation_Factor: [false],
          CALCULATION_CO2_Carbon_Content: [false],
          CALCULATION_CO2_Conversion_Factor: [false],
          CALCULATION_CO2_Biomass_Fraction: [false],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.emitterId = '';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Enter an installation emitter ID']);

      page.emitterId = '77777';

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...mockState,
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              CALCULATION_CO2: {
                ...mockState.permit.monitoringApproaches.CALCULATION_CO2,
                sourceStreamCategoryAppliedTiers: [
                  {
                    ...(mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
                      .sourceStreamCategoryAppliedTiers[0],
                    sourceStreamCategory: {
                      ...(mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
                        .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory,
                      transfer: {
                        ...(mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
                          .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory.transfer,
                        installationDetails: null,
                        installationEmitter: {
                          ...(
                            mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
                          ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory.transfer.installationEmitter,
                          emitterId: '77777',
                        },
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            CALCULATION_CO2_Category: [true],
            CALCULATION_CO2_Activity_Data: [false],
            CALCULATION_CO2_Calorific: [false],
            CALCULATION_CO2_Emission_Factor: [false],
            CALCULATION_CO2_Oxidation_Factor: [false],
            CALCULATION_CO2_Carbon_Content: [false],
            CALCULATION_CO2_Conversion_Factor: [false],
            CALCULATION_CO2_Biomass_Fraction: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });
});
