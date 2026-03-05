import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import {
  AerApplicationSubmitRequestTaskPayload,
  MeasurementEmissionsCalculationParamsDTO,
  ReportingService,
} from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { buildTaskData } from '../measurement';
import { AER_MEASUREMENT_FORM, getCompletionStatus } from '../measurement-status';
import { gasFlowFormProvider } from './gas-flow-form.provider';

@Component({
  selector: 'app-gas-flow',
  templateUrl: './gas-flow.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [gasFlowFormProvider],
})
export class GasFlowComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  taskKey = this.route.snapshot.data.taskKey;
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_MEASUREMENT_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    readonly reportingService: ReportingService,
  ) {}

  onContinue(): void {
    combineLatest([this.payload$, this.index$])
      .pipe(
        first(),
        switchMap(([payload, index]) => {
          const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
          const measurement = monitoringApproachEmissions[this.taskKey] as any;
          const emissionPointEmission = measurement.emissionPointEmissions?.[index];

          const parameters: MeasurementEmissionsCalculationParamsDTO = {
            annualHourlyAverageGHGConcentration: emissionPointEmission.annualHourlyAverageGHGConcentration,
            annualHourlyAverageFlueGasFlow: this.form.get('annualHourlyAverageFlueGasFlow').value,
            biomassPercentage: emissionPointEmission.biomassPercentages.biomassPercentage,
            containsBiomass: emissionPointEmission.biomassPercentages.contains,
            operationalHours: emissionPointEmission.operationalHours,
          };

          return this.taskKey === 'MEASUREMENT_CO2'
            ? this.reportingService.calculateMeasurementCO2Emissions(parameters)
            : this.reportingService.calculateMeasurementN2OEmissions(parameters);
        }),
        withLatestFrom(this.payload$, this.index$),
        switchMap(([totals, payload, index]) =>
          this.aerService.postTaskSave(
            this.buildData(totals, payload, index),
            undefined,
            getCompletionStatus(this.taskKey, payload, index, false),
            this.taskKey,
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private buildData(totals, payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const measurement = monitoringApproachEmissions[this.taskKey] as any;

    const emissionPointEmissions = measurement.emissionPointEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            annualHourlyAverageFlueGasFlow: this.form.get('annualHourlyAverageFlueGasFlow').value,
            annualFossilAmountOfGreenhouseGas: totals.annualFossilAmountOfGreenhouseGas,
            annualGasFlow: totals.annualGasFlow,
            globalWarmingPotential: totals.globalWarmingPotential,
            reportableEmissions: totals.reportableEmissions,
            sustainableBiomassEmissions: totals.sustainableBiomassEmissions,
            calculationCorrect: null,
          }
        : item,
    );

    const data = buildTaskData(this.taskKey, payload, emissionPointEmissions);

    return data;
  }
  private navigateNext() {
    const nextStep = 'calculation-review';

    this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
  }
}
