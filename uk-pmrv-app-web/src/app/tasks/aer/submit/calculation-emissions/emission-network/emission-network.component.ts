import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, Observable, switchMap, takeUntil } from 'rxjs';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
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
import { emissionNetworkFormProvider } from './emission-network.provider';

@Component({
  selector: 'app-emission-network',
  templateUrl: './emission-network.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionNetworkFormProvider, DestroySubject],
})
export class EmissionNetworkComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  isEditable$ = this.aerService.isEditable$;

  sourceStreamEmission$: Observable<CalculationSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    filter(([payload]) => !!payload),
    map(([payload, index]) => {
      const res = (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
        ?.sourceStreamEmissions?.[index];
      return res;
    }),
  );

  private hasTransfer$: Observable<boolean> = this.payload$.pipe(
    map(
      (payload) => (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.hasTransfer,
    ),
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

  onDelete() {
    const nextStep = '../delete';
    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

    const sourceStreamEmission = calculation?.sourceStreamEmissions?.[index];
    const containsBiomass = this.form.get('containsBiomass').value;

    const parameterCalculationMethod = buildParameterCalculationMethodData(sourceStreamEmission);

    const parameterMonitoringTiers = sourceStreamEmission?.parameterMonitoringTiers?.filter(
      (parameterMonitoringTiers) => {
        return (
          parameterMonitoringTiers?.type !== 'BIOMASS_FRACTION' ||
          (containsBiomass && parameterMonitoringTiers?.type === 'BIOMASS_FRACTION')
        );
      },
    );

    const formData = {
      sourceStream: this.form.get('sourceStream').value,
      emissionSources: this.form.get('emissionSources').value,
      biomassPercentages: {
        ...(containsBiomass ? calculation?.sourceStreamEmissions?.[index]?.biomassPercentages : {}),
        contains: containsBiomass,
      },
    };

    let sourceStreamEmissionPayload = {
      ...sourceStreamEmission,
      ...formData,
      ...parameterCalculationMethod,
      parameterMonitoringTiers,
    };

    if (this.form.get('sourceStream').value !== sourceStreamEmission?.sourceStream) {
      sourceStreamEmissionPayload = Object.keys(sourceStreamEmissionPayload)
        .filter((key) => key !== 'parameterMonitoringTiers' && key !== 'parameterCalculationMethod')
        .reduce((result, key) => ({ ...result, [key]: sourceStreamEmissionPayload[key] }), {}) as any;
    }

    const sourceStreamEmissions =
      calculation?.sourceStreamEmissions && calculation.sourceStreamEmissions?.[index]
        ? calculation.sourceStreamEmissions.map((item, idx) => {
            return idx === index
              ? {
                  ...sourceStreamEmissionPayload,
                }
              : item;
          })
        : [...(calculation?.sourceStreamEmissions ?? []), sourceStreamEmissionPayload];

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }
  private navigateNext() {
    this.hasTransfer$.pipe(takeUntil(this.destroy$)).subscribe((hasTransfer) => {
      let nextStep = '';

      if (hasTransfer === true) {
        nextStep = 'transferred';
      } else {
        nextStep = 'date-range';
      }
      this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
    });
  }
}
