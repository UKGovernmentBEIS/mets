import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap, takeUntil } from 'rxjs';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationNationalInventoryDataCalculationMethod,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
  CalculationSourceStreamEmission,
} from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import {
  AER_CALCULATION_EMISSIONS_FORM,
  buildParameterCalculationMethodData,
  buildTaskData,
} from '../calculation-emissions';
import { biomassCalculationFormProvider } from './biomass-calculation.provider';

@Component({
  selector: 'app-biomass-calculation',
  templateUrl: './biomass-calculation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [biomassCalculationFormProvider, DestroySubject],
})
export class BiomassCalculationComponent {
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

  constructor(
    @Inject(AER_CALCULATION_EMISSIONS_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    private readonly destroy$: DestroySubject,
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

    const parameterCalculationMethod = buildParameterCalculationMethodData(calculation.sourceStreamEmissions?.[index]);

    const biomassPercentages = {
      contains: true,
      biomassPercentage: this.form.get('biomassPercentage').value,
      nonSustainableBiomassPercentage: this.form.get('nonSustainableBiomassPercentage').value,
    };

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...parameterCalculationMethod,
            biomassPercentages,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }
  private navigateNext() {
    this.sourceStreamEmission$.pipe(takeUntil(this.destroy$)).subscribe((sourceStreamEmission) => {
      let nextStep = '';

      const filledInventoryDataFields =
        ['NATIONAL_INVENTORY_DATA', 'REGIONAL_DATA'].includes(sourceStreamEmission?.parameterCalculationMethod.type) &&
        (!!(sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod)
          ?.localZoneCode ||
          !!(sourceStreamEmission?.parameterCalculationMethod as CalculationNationalInventoryDataCalculationMethod)
            ?.mainActivitySector);

      if (filledInventoryDataFields) {
        nextStep = 'inventory-data-review';
      } else {
        nextStep = 'manual-calculation-values';
      }
      this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
    });
  }
}
