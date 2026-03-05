import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationManualCalculationMethod,
  CalculationOfCO2Emissions,
  CalculationSourceStreamEmission,
} from 'pmrv-api';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { AerService } from '../../../../core/aer.service';
import { getCompletionStatus } from '../../../../shared/components/submit/emissions-status';
import { AER_CALCULATION_EMISSIONS_FORM, buildTaskData } from '../../calculation-emissions';
import { getEmissionsElements } from '../calculation-reviews';
import { manualDataReviewFormProvider } from './manual-data-review.provider';

@Component({
  selector: 'app-inventory-data-review',
  templateUrl: './manual-data-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [manualDataReviewFormProvider],
})
export class ManualDataReviewComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  isEditable$ = this.aerService.isEditable$;

  sourceStreamEmission$: Observable<CalculationSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      const res = (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
        ?.sourceStreamEmissions?.[index];
      return res;
    }),
  );

  emissionsElements$ = combineLatest([this.sourceStreamEmission$]).pipe(
    map(([sourceStreamEmission]) => {
      const calculatedEmissions = (
        sourceStreamEmission?.parameterCalculationMethod as CalculationManualCalculationMethod
      )?.emissionCalculationParamValues;

      return getEmissionsElements(sourceStreamEmission, calculatedEmissions);
    }),
  );

  constructor(
    @Inject(AER_CALCULATION_EMISSIONS_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.navigateNext();
    } else {
      combineLatest([this.payload$, this.index$])
        .pipe(
          first(),
          switchMap(([payload, index]) =>
            this.aerService.postTaskSave(
              this.buildData(payload, index),
              undefined,
              getCompletionStatus('CALCULATION_CO2', payload, index, false),
              'CALCULATION_CO2',
            ),
          ),

          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;
    const sourceStreamEmission = calculation.sourceStreamEmissions?.[index];

    const parameterCalculationMethod =
      sourceStreamEmission?.parameterCalculationMethod as CalculationManualCalculationMethod;

    const calculationCorrect = this.form.controls.calculationCorrect.value;

    const formPayload = {
      parameterCalculationMethod: {
        ...parameterCalculationMethod,
        emissionCalculationParamValues: {
          ...parameterCalculationMethod.emissionCalculationParamValues,
          calculationCorrect: calculationCorrect,
          ...(!calculationCorrect
            ? {
                providedEmissions: {
                  reasonForProvidingManualEmissions: this.form.controls.reasonForProvidingManualEmissions.value,
                  totalProvidedReportableEmissions: this.form.controls.totalProvidedReportableEmissions.value,
                  ...(sourceStreamEmission?.biomassPercentages?.contains
                    ? {
                        totalProvidedSustainableBiomassEmissions:
                          this.form.controls.totalProvidedSustainableBiomassEmissions.value,
                      }
                    : {}),
                },
              }
            : {
                providedEmissions: null,
              }),
        },
      },
    };

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...formPayload,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    const nextStep = '../summary';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
