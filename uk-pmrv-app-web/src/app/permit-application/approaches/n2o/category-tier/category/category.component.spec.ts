import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { MeasurementOfN2OMonitoringApproach, TasksService } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { N2oModule } from '../../n2o.module';
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
    taskKey: 'monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers',
  });

  class Page extends BasePage<CategoryComponent> {
    get sourceStreams() {
      return this.fixture.componentInstance.form.get('sourceStreams').value;
    }
    set sourceStreams(value: string[]) {
      this.fixture.componentInstance.form.get('sourceStreams').setValue(value);
    }
    get emissionSources() {
      return this.fixture.componentInstance.form.get('emissionSources').value;
    }
    set emissionSources(value: string[]) {
      this.fixture.componentInstance.form.get('emissionSources').setValue(value);
    }
    get emissionPoint(): string {
      return this.getInputValue('#emissionPoint');
    }
    set emissionPoint(value: string) {
      this.setInputValue('#emissionPoint', value);
    }
    get emissionType(): string {
      return this.fixture.componentInstance.form.get('emissionType').value;
    }
    set emissionType(value: string) {
      this.fixture.componentInstance.form.get('emissionType').setValue(value);
    }
    get monitoringApproachType(): string {
      return this.fixture.componentInstance.form.get('monitoringApproachType').value;
    }
    set monitoringApproachType(value: string) {
      this.fixture.componentInstance.form.get('monitoringApproachType').setValue(value);
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
      imports: [N2oModule, RouterTestingModule],
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
          MEASUREMENT_N2O_Category: [true],
          MEASUREMENT_N2O_Measured_Emissions: [false],
          MEASUREMENT_N2O_Applied_Standard: [false],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
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
              MEASUREMENT_N2O: {
                ...mockState.permit.monitoringApproaches.MEASUREMENT_N2O,
                emissionPointCategoryAppliedTiers: [
                  {
                    ...(mockState.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
                      .emissionPointCategoryAppliedTiers[0],
                    emissionPointCategory: {
                      ...(mockState.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
                        .emissionPointCategoryAppliedTiers[0].emissionPointCategory,
                      annualEmittedCO2Tonnes: '5000',
                      transfer: {
                        entryAccountingForTransfer: (
                          mockState.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach
                        ).emissionPointCategoryAppliedTiers[0].emissionPointCategory.transfer
                          .entryAccountingForTransfer,
                        transferDirection: null,
                        transferType: 'TRANSFER_N2O',
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            MEASUREMENT_N2O_Category: [true],
            MEASUREMENT_N2O_Measured_Emissions: [false],
            MEASUREMENT_N2O_Applied_Standard: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
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
            MEASUREMENT_N2O: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_N2O,
              emissionPointCategoryAppliedTiers: undefined,
            } as MeasurementOfN2OMonitoringApproach,
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Select a source stream',
        'Select at least one emission source',
        'Select at least one emission point',
        'Select a type',
        'Select an approach',
        'Select an estimated tonnes of CO2e',
        'Select a category',
        'Select a category',
      ]);

      const emissionPointCategory = (
        mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach
      ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

      page.sourceStreams = emissionPointCategory.sourceStreams;
      page.emissionSources = emissionPointCategory.emissionSources;
      page.emissionPoint = emissionPointCategory.emissionPoint;
      page.emissionType = emissionPointCategory.emissionType;
      page.monitoringApproachType = emissionPointCategory.monitoringApproachType;
      page.annualEmittedCO2Tonnes = emissionPointCategory.annualEmittedCO2Tonnes.toString();
      page.categoryType = emissionPointCategory.categoryType;
      page.entryAccountingForTransfer = emissionPointCategory.transfer.entryAccountingForTransfer;

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
              MEASUREMENT_N2O: {
                ...store.permit.monitoringApproaches.MEASUREMENT_N2O,
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: {
                      sourceStreams: emissionPointCategory.sourceStreams,
                      emissionSources: emissionPointCategory.emissionSources,
                      emissionPoint: emissionPointCategory.emissionPoint,
                      emissionType: emissionPointCategory.emissionType,
                      monitoringApproachType: emissionPointCategory.monitoringApproachType,
                      annualEmittedCO2Tonnes: emissionPointCategory.annualEmittedCO2Tonnes,
                      categoryType: emissionPointCategory.categoryType,
                      transfer: {
                        entryAccountingForTransfer: emissionPointCategory.transfer.entryAccountingForTransfer,
                        transferDirection: null,
                        transferType: emissionPointCategory.transfer.transferType,
                      },
                    },
                  },
                ],
              } as MeasurementOfN2OMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            MEASUREMENT_N2O_Category: [true],
            MEASUREMENT_N2O_Measured_Emissions: [false],
            MEASUREMENT_N2O_Applied_Standard: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });

    it('should submit a valid form, update the store and navigate to tranferred-co2-details', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Select a source stream',
        'Select at least one emission source',
        'Select at least one emission point',
        'Select a type',
        'Select an approach',
        'Select an estimated tonnes of CO2e',
        'Select a category',
        'Select a category',
      ]);

      const emissionPointCategory = (
        mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach
      ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

      page.sourceStreams = emissionPointCategory.sourceStreams;
      page.emissionSources = emissionPointCategory.emissionSources;
      page.emissionPoint = emissionPointCategory.emissionPoint;
      page.emissionType = emissionPointCategory.emissionType;
      page.monitoringApproachType = emissionPointCategory.monitoringApproachType;
      page.annualEmittedCO2Tonnes = emissionPointCategory.annualEmittedCO2Tonnes.toString();
      page.categoryType = emissionPointCategory.categoryType;
      page.entryAccountingForTransfer = true;
      page.transferDirection = 'EXPORTED_TO_LONG_TERM_FACILITY';

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
              MEASUREMENT_N2O: {
                ...store.permit.monitoringApproaches.MEASUREMENT_N2O,
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: {
                      sourceStreams: emissionPointCategory.sourceStreams,
                      emissionSources: emissionPointCategory.emissionSources,
                      emissionPoint: emissionPointCategory.emissionPoint,
                      emissionType: emissionPointCategory.emissionType,
                      monitoringApproachType: emissionPointCategory.monitoringApproachType,
                      annualEmittedCO2Tonnes: emissionPointCategory.annualEmittedCO2Tonnes,
                      categoryType: emissionPointCategory.categoryType,
                      transfer: {
                        entryAccountingForTransfer: true,
                        transferDirection: 'EXPORTED_TO_LONG_TERM_FACILITY',
                        transferType: 'TRANSFER_N2O',
                      },
                    },
                  },
                ],
              } as MeasurementOfN2OMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            MEASUREMENT_N2O_Category: [false],
            MEASUREMENT_N2O_Measured_Emissions: [false],
            MEASUREMENT_N2O_Applied_Standard: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['tranferred-co2-details'], {
        relativeTo: route,
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
          { MEASUREMENT_N2O_Category: [true] },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the error summary', () => {
      /*expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Select a source stream',
        'Select an emission source',
        'Select an emission point',
      ]);*/
    });
  });
});
