import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { ApproachTaskPipe } from '@permit-application/approaches/approach-task.pipe';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';

import {
  CalculationOfCO2MonitoringApproach,
  CalculationSourceStreamCategory,
  CalculationSourceStreamCategoryAppliedTier,
} from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
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
  sourceStream$ = this.index$.pipe(
    switchMap((index) =>
      this.approachTaskPipe.transform('CALCULATION_CO2').pipe(
        map((response) => {
          return response?.sourceStreamCategoryAppliedTiers?.[index]?.sourceStreamCategory.sourceStream;
        }),
      ),
    ),
  );

  sourceStreamCategory$ = this.index$.pipe(
    switchMap((index) =>
      this.approachTaskPipe.transform('CALCULATION_CO2').pipe(
        map((response) => {
          return response?.sourceStreamCategoryAppliedTiers?.[index]?.sourceStreamCategory;
        }),
      ),
    ),
  );
  hasTransfer$ = this.store.pipe(
    map(
      (state) =>
        (state.permit?.monitoringApproaches?.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)?.hasTransfer,
    ),
  );
  calculationMethodOptions = [
    {
      label: 'Standard calculation',
      value: 'STANDARD',
      hint: 'Where total CO2 is determined solely by calculating activity data (amount of a fuel or material used) alongside other relevant calculation parameters',
    },
    {
      label: 'Mass balance',
      value: 'MASS_BALANCE',
      hint: 'Where total CO2 is determined by considering the amount of a fuel or material before and after some process has occurred to release CO2',
    },
  ];
  categoryTypeOptions: CalculationSourceStreamCategory['categoryType'][] = ['MAJOR', 'MINOR', 'DE_MINIMIS', 'MARGINAL'];

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
      this.store.findTask<CalculationSourceStreamCategoryAppliedTier[]>(
        'monitoringApproaches.CALCULATION_CO2.sourceStreamCategoryAppliedTiers',
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
                CALCULATION_CO2: {
                  ...state.permit.monitoringApproaches.CALCULATION_CO2,
                  sourceStreamCategoryAppliedTiers:
                    tiers && tiers[index]
                      ? tiers.map((tier, idx) =>
                          index === idx
                            ? {
                                ...tier,
                                sourceStreamCategory: {
                                  annualEmittedCO2Tonnes: this.form.controls.annualEmittedCO2Tonnes.value.toString(),
                                  calculationMethod: this.form.controls.calculationMethod.value,
                                  categoryType: this.form.controls.categoryType.value,
                                  emissionSources: this.form.controls.emissionSources.value,
                                  sourceStream: this.form.controls.sourceStream.value,
                                  ...((
                                    this.store.permit.monitoringApproaches
                                      .CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
                                  )?.hasTransfer
                                    ? {
                                        transfer: {
                                          ...(this.form.controls.entryAccountingForTransfer.value
                                            ? tier.sourceStreamCategory?.transfer
                                            : {}),
                                          transferType: 'TRANSFER_CO2',
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
                            sourceStreamCategory: {
                              annualEmittedCO2Tonnes: this.form.controls.annualEmittedCO2Tonnes.value.toString(),
                              calculationMethod: this.form.controls.calculationMethod.value,
                              categoryType: this.form.controls.categoryType.value,
                              emissionSources: this.form.controls.emissionSources.value,
                              sourceStream: this.form.controls.sourceStream.value,
                              ...((
                                this.store.permit.monitoringApproaches
                                  .CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
                              )?.hasTransfer
                                ? {
                                    transfer: {
                                      transferType: 'TRANSFER_CO2',
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
                } as CalculationOfCO2MonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              CALCULATION_CO2_Category:
                tiers && tiers[index]
                  ? state.permitSectionsCompleted.CALCULATION_CO2_Category.map((item, idx) => {
                      if (index === idx) {
                        if (!this.form.controls.entryAccountingForTransfer.value) {
                          return true;
                        } else return false;
                      } else return item;
                    })
                  : [
                      ...(state.permitSectionsCompleted.CALCULATION_CO2_Category ?? []),
                      !this.form.controls.entryAccountingForTransfer.value,
                    ],

              ...this.buildPermitSectionCompletedFactor(state, tiers, index, 'CALCULATION_CO2_Activity_Data'),
              ...this.buildPermitSectionCompletedFactor(state, tiers, index, 'CALCULATION_CO2_Calorific'),
              ...this.buildPermitSectionCompletedFactor(state, tiers, index, 'CALCULATION_CO2_Emission_Factor'),
              ...this.buildPermitSectionCompletedFactor(state, tiers, index, 'CALCULATION_CO2_Oxidation_Factor'),
              ...this.buildPermitSectionCompletedFactor(state, tiers, index, 'CALCULATION_CO2_Carbon_Content'),
              ...this.buildPermitSectionCompletedFactor(state, tiers, index, 'CALCULATION_CO2_Conversion_Factor'),
              ...this.buildPermitSectionCompletedFactor(state, tiers, index, 'CALCULATION_CO2_Biomass_Fraction'),
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
    tiers: CalculationSourceStreamCategoryAppliedTier[],
    index: number,
    status: boolean[],
  ): boolean[] {
    return tiers && tiers[index] ? status : [...(status ?? []), false];
  }

  private buildPermitSectionCompletedFactor(
    state: PermitApplicationState,
    tiers: CalculationSourceStreamCategoryAppliedTier[],
    index: number,
    factor: string,
  ) {
    return {
      [factor]: this.applySubtaskStatus(tiers, index, state.permitSectionsCompleted[factor]),
    };
  }
}
