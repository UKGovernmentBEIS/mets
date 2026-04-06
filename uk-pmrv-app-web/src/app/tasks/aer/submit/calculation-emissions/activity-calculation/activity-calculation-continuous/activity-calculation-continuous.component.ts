import { ChangeDetectionStrategy, Component, Signal } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, Observable, shareReplay, switchMap, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
  CalculationSourceStreamEmission,
} from 'pmrv-api';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../../../core/services/destroy-subject.service';
import { AerService } from '../../../../core/aer.service';
import { getCompletionStatus } from '../../../../shared/components/submit/emissions-status';
import {
  buildParameterCalculationMethodData,
  buildTaskData,
  maxIntegerPartValidator,
} from '../../calculation-emissions';
import { NationalInventoryService } from '../../services/national-inventory.service';
import { RegionalInventoryService } from '../../services/regional-inventory.service';
import {
  calculateActivityData,
  getActivityCalculationNextStep,
  getActivityDataMeasuremenUnits,
  getFormData,
} from '../activity-calculation';
@Component({
  selector: 'app-activity-calculation-continuous',
  standalone: false,
  templateUrl: './activity-calculation-continuous.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityCalculationContinuousComponent {
  measurementUnits = getActivityDataMeasuremenUnits();

  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  isEditable$ = this.aerService.isEditable$;

  sourceStreamEmission$: Observable<CalculationSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    map(
      ([payload, index]) =>
        (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions?.[
          index
        ],
    ),
  );

  isSm3: Signal<boolean> = this.aerService.getIsSm3(this.index$);

  nationalInventoryData$ = this.nationalInventoryService.nationalInventoryData$;

  regionalInventoryData$ = this.sourceStreamEmission$.pipe(
    switchMap((sourceStreamEmission) =>
      this.regionalInventoryService.getRegionalInventoryData(
        (sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod)?.localZoneCode,
      ),
    ),
  );

  form$: Observable<UntypedFormGroup> = combineLatest([
    this.sourceStreamEmission$,
    this.nationalInventoryData$,
    this.regionalInventoryData$,
    this.isEditable$,
    this.aerService.getIsSm3$(this.index$),
  ]).pipe(
    map(([sourceStreamEmission, nationalInventoryData, regionalInventoryData, isEditable, isSm3]) => {
      const [predefinedMeasurementUnit, calculationActivityDataCalculationMethod] = getFormData(
        sourceStreamEmission,
        nationalInventoryData,
        regionalInventoryData,
      );

      const controls = this.getFormControls(
        predefinedMeasurementUnit,
        calculationActivityDataCalculationMethod,
        isEditable,
        isSm3,
      );

      return this.fb.group(controls);
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    private readonly fb: UntypedFormBuilder,
    readonly aerService: AerService,
    readonly nationalInventoryService: NationalInventoryService,
    readonly regionalInventoryService: RegionalInventoryService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    private readonly destroy$: DestroySubject,
  ) {}

  onContinue(): void {
    combineLatest([this.form$, this.payload$, this.index$, this.regionalInventoryData$])
      .pipe(
        first(),
        filter(([form]) => form.valid),
        switchMap(([form, payload, index, regionalInventoryData]) =>
          this.aerService.postTaskSave(
            this.buildData(form, payload, index, regionalInventoryData),
            undefined,
            getCompletionStatus('CALCULATION_CO2', payload, index, false),
            'CALCULATION_CO2',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private buildData(form, payload: AerApplicationSubmitRequestTaskPayload, index: number, regionalInventoryData) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

    const sourceStreamEmission = calculation.sourceStreamEmissions?.[index];

    const measurementUnit = form.get('measurementUnit').value ?? 'NM3';
    const totalMaterial = form.get('totalMaterial').value;

    const activityData = calculateActivityData(
      sourceStreamEmission,
      totalMaterial,
      regionalInventoryData?.calculationFactor,
    );

    const parameterCalculationMethodData = {
      calculationActivityDataCalculationMethod: {
        ...calculation.sourceStreamEmissions?.[index]?.parameterCalculationMethod
          ?.calculationActivityDataCalculationMethod,
        measurementUnit,
        totalMaterial,
        activityData,
      },
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
    this.sourceStreamEmission$.pipe(takeUntil(this.destroy$)).subscribe((sourceStreamEmission) => {
      const nextStep = getActivityCalculationNextStep(sourceStreamEmission);

      this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
    });
  }

  private getFormControls(predefinedMeasurementUnit, calculationActivityDataCalculationMethod, isEditable, isSm3) {
    const controls = {
      measurementUnit: [
        {
          value: isSm3
            ? null
            : predefinedMeasurementUnit === 'GJ_PER_TONNE'
              ? 'TONNES'
              : predefinedMeasurementUnit === 'GJ_PER_NM3'
                ? 'NM3'
                : calculationActivityDataCalculationMethod?.measurementUnit
                  ? calculationActivityDataCalculationMethod.measurementUnit
                  : null,
          disabled: !isEditable || !!predefinedMeasurementUnit,
        },
        isSm3
          ? {}
          : {
              validators: GovukValidators.required('Please select a measurement unit'),
            },
      ],
      totalMaterial: [
        {
          value: calculationActivityDataCalculationMethod?.totalMaterial ?? null,
          disabled: !isEditable,
        },
        {
          validators: [
            GovukValidators.required('Please state the total amount of exported fuel'),
            maxIntegerPartValidator(), // TODO should be replaced with rangeIntegerPartValidator(),
          ],
        },
      ],
    };

    return controls;
  }
}
