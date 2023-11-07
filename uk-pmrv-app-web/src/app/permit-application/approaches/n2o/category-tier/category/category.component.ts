import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { ApproachTaskPipe } from '@permit-application/approaches/approach-task.pipe';

import {
  MeasurementOfN2OEmissionPointCategory,
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
  MeasurementOfN2OMonitoringApproach,
} from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryFormProvider } from './category-form.provider';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [categoryFormProvider],
})
export class CategoryComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  emissionPoint$ = this.index$.pipe(
    switchMap((index) =>
      this.approachTaskPipe.transform('MEASUREMENT_N2O').pipe(
        map((response) => {
          return response?.emissionPointCategoryAppliedTiers?.[index]?.emissionPointCategory.emissionPoint;
        }),
      ),
    ),
  );

  emissionPointCategory$ = this.index$.pipe(
    switchMap((index) =>
      this.approachTaskPipe.transform('MEASUREMENT_N2O').pipe(
        map((response) => {
          return response?.emissionPointCategoryAppliedTiers?.[index]?.emissionPointCategory;
        }),
      ),
    ),
  );

  hasTransfer$ = this.store.pipe(
    map(
      (state) =>
        (state.permit?.monitoringApproaches?.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)?.hasTransfer,
    ),
  );
  emissionTypeOptions = [
    { label: 'Abated', value: 'ABATED', hint: '' },
    { label: 'Unabated', value: 'UNABATED', hint: '' },
  ];
  monitoringApproachTypeOptions = [
    { label: 'Calculation', value: 'CALCULATION' },
    { label: 'Measurement', value: 'MEASUREMENT' },
  ];
  categoryTypeOptions: MeasurementOfN2OEmissionPointCategory['categoryType'][] = [
    'MAJOR',
    'MINOR',
    'DE_MINIMIS',
    'MARGINAL',
  ];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private approachTaskPipe: ApproachTaskPipe,
  ) {}

  onSubmit(): void {
    combineLatest([
      this.index$,
      this.store.findTask<MeasurementOfN2OEmissionPointCategoryAppliedTier[]>(
        'monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers',
      ),
      this.store,
      this.route.data,
    ])
      .pipe(
        first(),
        switchMap(([index, tiers, state, data]) =>
          this.store.postCategoryTask(data.taskKey, {
            ...state,
            permit: {
              ...state.permit,
              monitoringApproaches: {
                ...state.permit.monitoringApproaches,
                MEASUREMENT_N2O: {
                  ...state.permit.monitoringApproaches.MEASUREMENT_N2O,
                  emissionPointCategoryAppliedTiers:
                    tiers && tiers[index]
                      ? tiers.map((tier, idx) =>
                          index === idx
                            ? {
                                ...tier,
                                emissionPointCategory: {
                                  sourceStreams: this.form.controls.sourceStreams.value,
                                  emissionSources: this.form.controls.emissionSources.value,
                                  emissionPoint: this.form.controls.emissionPoint.value,
                                  emissionType: this.form.controls.emissionType.value,
                                  monitoringApproachType: this.form.controls.monitoringApproachType.value,
                                  annualEmittedCO2Tonnes: this.form.controls.annualEmittedCO2Tonnes.value.toString(),
                                  categoryType: this.form.controls.categoryType.value,
                                  ...((
                                    this.store.permit.monitoringApproaches
                                      .MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach
                                  )?.hasTransfer
                                    ? {
                                        transfer: {
                                          ...(this.form.controls.entryAccountingForTransfer.value
                                            ? tier.emissionPointCategory?.transfer
                                            : {}),
                                          transferType: 'TRANSFER_N2O',
                                          entryAccountingForTransfer:
                                            this.form.controls.entryAccountingForTransfer.value,
                                          transferDirection: this.form.controls.entryAccountingForTransfer.value
                                            ? this.form.controls.transferDirection.value
                                            : null,
                                        },
                                      }
                                    : {}),
                                },
                              }
                            : tier,
                        )
                      : [
                          ...(tiers ?? []),
                          {
                            emissionPointCategory: {
                              sourceStreams: this.form.controls.sourceStreams.value,
                              emissionSources: this.form.controls.emissionSources.value,
                              emissionPoint: this.form.controls.emissionPoint.value,
                              emissionType: this.form.controls.emissionType.value,
                              monitoringApproachType: this.form.controls.monitoringApproachType.value,
                              annualEmittedCO2Tonnes: this.form.controls.annualEmittedCO2Tonnes.value.toString(),
                              categoryType: this.form.controls.categoryType.value,
                              ...((
                                this.store.permit.monitoringApproaches
                                  .MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach
                              )?.hasTransfer
                                ? {
                                    transfer: {
                                      transferType: 'TRANSFER_N2O',
                                      entryAccountingForTransfer: this.form.controls.entryAccountingForTransfer.value,
                                      transferDirection: this.form.controls.entryAccountingForTransfer.value
                                        ? this.form.controls.transferDirection.value
                                        : null,
                                    },
                                  }
                                : {}),
                            },
                          },
                        ],
                } as MeasurementOfN2OMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              MEASUREMENT_N2O_Category:
                tiers && tiers[index]
                  ? state.permitSectionsCompleted.MEASUREMENT_N2O_Category.map((item, idx) => {
                      if (index === idx) {
                        if (!this.form.controls.entryAccountingForTransfer.value) {
                          return true;
                        } else return false;
                      } else return item;
                    })
                  : [
                      ...(state.permitSectionsCompleted.MEASUREMENT_N2O_Category ?? []),
                      !this.form.controls.entryAccountingForTransfer.value,
                    ],
              ...this.buildPermitSectionCompletedFactor(state, tiers, index, 'MEASUREMENT_N2O_Measured_Emissions'),
              ...this.buildPermitSectionCompletedFactor(state, tiers, index, 'MEASUREMENT_N2O_Applied_Standard'),
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        if (this.form.controls.entryAccountingForTransfer.value) {
          this.router.navigate(['tranferred-co2-details'], { relativeTo: this.route });
        } else this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } });
      });
  }

  private applySubtaskStatus(
    tiers: MeasurementOfN2OEmissionPointCategoryAppliedTier[],
    index: number,
    status: boolean[],
  ): boolean[] {
    return tiers && tiers[index] ? status : [...(status ?? []), false];
  }

  private buildPermitSectionCompletedFactor(
    state: PermitApplicationState,
    tiers: MeasurementOfN2OEmissionPointCategoryAppliedTier[],
    index: number,
    factor: string,
  ) {
    return {
      [factor]: this.applySubtaskStatus(tiers, index, state.permitSectionsCompleted[factor]),
    };
  }
}
