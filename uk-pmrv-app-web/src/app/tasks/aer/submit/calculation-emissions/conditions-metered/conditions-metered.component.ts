import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfCO2Emissions } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import {
  AER_CALCULATION_EMISSIONS_FORM,
  buildParameterCalculationMethodData,
  buildTaskData,
} from '../calculation-emissions';
import { conditionsMeteredFormProvider } from './conditions-metered.provider';

@Component({
  selector: 'app-conditions-metered',
  templateUrl: './conditions-metered.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [conditionsMeteredFormProvider],
})
export class ConditionsMeteredComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_CALCULATION_EMISSIONS_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
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

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

    const parameterCalculationMethodData = {
      fuelMeteringConditionType: this.form.get('fuelMeteringConditionType').value,
    };

    const parameterCalculationMethod = buildParameterCalculationMethodData(
      calculation.sourceStreamEmissions?.[index],
      parameterCalculationMethodData,
    );

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...parameterCalculationMethod,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    const nextStep = '../activity-calculation-method';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
