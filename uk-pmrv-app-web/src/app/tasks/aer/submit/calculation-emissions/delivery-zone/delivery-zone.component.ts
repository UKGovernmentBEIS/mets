import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, shareReplay, switchMap } from 'rxjs';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
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
import { deliveryZoneFormProvider } from './delivery-zone.provider';

@Component({
  selector: 'app-delivery-zone',
  templateUrl: './delivery-zone.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [deliveryZoneFormProvider, DestroySubject],
})
export class DeliveryZoneComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  isEditable$ = this.aerService.isEditable$;

  parameterCalculationMethod$ = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      return (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
        ?.sourceStreamEmissions?.[index]?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod;
    }),
  );

  deliveryZones$ = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      const parameterCalculationMethod = (
        payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions
      )?.sourceStreamEmissions?.[index]?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod;

      return parameterCalculationMethod?.postCode;
    }),
    switchMap((postCode) => this.aerService.getDeliveryZones(postCode)),
    shareReplay({ bufferSize: 1, refCount: true }),
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

    const parameterCalculationMethodData = {
      localZoneCode: this.form.get('localZoneCode').value,
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
    const nextStep = '../conditions-metered';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
