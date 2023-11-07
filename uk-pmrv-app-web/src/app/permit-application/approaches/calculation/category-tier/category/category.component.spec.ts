import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { CalculationOfCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { CategoryComponent } from './category.component';

describe('CategoryComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let activatedRoute: ActivatedRouteSnapshotStub;
  let component: CategoryComponent;
  let fixture: ComponentFixture<CategoryComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '237', index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION_CO2.sourceStreamCategoryAppliedTiers',
  });

  class Page extends BasePage<CategoryComponent> {
    get sourceStream(): string {
      return this.getInputValue('#sourceStream');
    }
    set sourceStream(value: string) {
      this.setInputValue('#sourceStream', value);
    }
    get emissionSources() {
      return this.fixture.componentInstance.form.get('emissionSources').value;
    }
    set emissionSources(value: string[]) {
      this.fixture.componentInstance.form.get('emissionSources').setValue(value);
    }
    get calculationMethod(): string {
      return this.fixture.componentInstance.form.get('calculationMethod').value;
    }
    set calculationMethod(value: string) {
      this.fixture.componentInstance.form.get('calculationMethod').setValue(value);
    }
    get annualEmittedCO2Tonnes(): string {
      return this.getInputValue('#annualEmittedCO2Tonnes');
    }
    set annualEmittedCO2Tonnes(value: string) {
      this.setInputValue('#annualEmittedCO2Tonnes', value);
    }
    get categoryType(): string {
      return this.fixture.componentInstance.form.get('categoryType').value;
    }
    set categoryType(value: string) {
      this.fixture.componentInstance.form.get('categoryType').setValue(value);
    }
    get entryAccountingForTransfer(): boolean {
      return this.fixture.componentInstance.form.get('entryAccountingForTransfer').value;
    }
    set entryAccountingForTransfer(value: boolean) {
      this.fixture.componentInstance.form.get('entryAccountingForTransfer').setValue(value);
    }
    get transferDirection(): string {
      return this.fixture.componentInstance.form.get('transferDirection').value;
    }
    set transferDirection(value: string) {
      this.fixture.componentInstance.form.get('transferDirection').setValue(value);
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
    fixture = TestBed.createComponent(CategoryComponent);
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

    it('should submit a valid form, update the store and navigate to tranferredco2details', () => {
      expect(page.errorSummary).toBeFalsy();

      page.annualEmittedCO2Tonnes = '';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['Select an estimated tonnes of CO2e']);

      page.annualEmittedCO2Tonnes = '5000';

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
                      annualEmittedCO2Tonnes: '5000',

                      transfer: {
                        transferType: 'TRANSFER_CO2',
                        entryAccountingForTransfer: (
                          mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
                        ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory.transfer.entryAccountingForTransfer,
                        installationDetailsType: 'INSTALLATION_EMITTER',
                        installationEmitter: {
                          email: 'test@beis.com',
                          emitterId: '12345',
                        },
                        transferDirection: (
                          mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
                        ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory.transfer.transferDirection,
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            CALCULATION_CO2_Category: [false],
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

      expect(navigateSpy).toHaveBeenCalledWith(['tranferred-co2-details'], {
        relativeTo: route,
      });
    });
  });

  describe('for new source stream category', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            CALCULATION_CO2: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_CO2,
              sourceStreamCategoryAppliedTiers: undefined,
            } as CalculationOfCO2MonitoringApproach,
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to tranferred-co2-details', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Select a source stream',
        'Select at least one emission source',
        'Select an estimated tonnes of CO2e',
        'Select a calculation method',
        'Select a category',
        'Select a category',
      ]);

      const sourceStreamCategory = (
        mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
      ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

      const transfer = (
        mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
      ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory.transfer;

      page.sourceStream = sourceStreamCategory.sourceStream;
      page.emissionSources = sourceStreamCategory.emissionSources;
      page.annualEmittedCO2Tonnes = sourceStreamCategory.annualEmittedCO2Tonnes.toString();
      page.calculationMethod = sourceStreamCategory.calculationMethod;
      page.categoryType = sourceStreamCategory.categoryType;
      page.entryAccountingForTransfer = transfer.entryAccountingForTransfer;
      page.transferDirection = transfer.transferDirection;

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.permit,
            monitoringApproaches: {
              ...store.permit.monitoringApproaches,
              CALCULATION_CO2: {
                ...store.permit.monitoringApproaches.CALCULATION_CO2,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: sourceStreamCategory.sourceStream,
                      emissionSources: sourceStreamCategory.emissionSources,
                      annualEmittedCO2Tonnes: sourceStreamCategory.annualEmittedCO2Tonnes,
                      calculationMethod: sourceStreamCategory.calculationMethod,
                      categoryType: sourceStreamCategory.categoryType,
                      transfer: {
                        transferType: transfer.transferType,
                        entryAccountingForTransfer: transfer.entryAccountingForTransfer,
                        transferDirection: transfer.transferDirection,
                      },
                    },
                  },
                ],
              } as CalculationOfCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            CALCULATION_CO2_Category: [false],
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

      expect(navigateSpy).toHaveBeenCalledWith(['tranferred-co2-details'], {
        relativeTo: route,
      });
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      const sourceStreamCategory = (
        mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
      ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

      page.sourceStream = sourceStreamCategory.sourceStream;
      page.emissionSources = sourceStreamCategory.emissionSources;
      page.annualEmittedCO2Tonnes = sourceStreamCategory.annualEmittedCO2Tonnes.toString();
      page.calculationMethod = sourceStreamCategory.calculationMethod;
      page.categoryType = sourceStreamCategory.categoryType;
      page.entryAccountingForTransfer = false;

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.permit,
            monitoringApproaches: {
              ...store.permit.monitoringApproaches,
              CALCULATION_CO2: {
                ...store.permit.monitoringApproaches.CALCULATION_CO2,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: sourceStreamCategory.sourceStream,
                      emissionSources: sourceStreamCategory.emissionSources,
                      annualEmittedCO2Tonnes: sourceStreamCategory.annualEmittedCO2Tonnes,
                      calculationMethod: sourceStreamCategory.calculationMethod,
                      categoryType: sourceStreamCategory.categoryType,
                      transfer: {
                        transferType: sourceStreamCategory.transfer.transferType,
                        entryAccountingForTransfer: false,
                        transferDirection: null,
                      },
                    },
                  },
                ],
              } as CalculationOfCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
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

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });

  describe('for editing source stream category with deleted reference data', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            sourceStreams: [],
            emissionSources: [],
            emissionPoints: [],
          },
          { CALCULATION_CO2_Category: [true] },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });
  });
});
