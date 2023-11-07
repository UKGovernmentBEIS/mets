import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, Observable, of, shareReplay, startWith, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { getCompletionStatus } from '@tasks/aer/shared/components/submit/emissions-status';
import BigNumber from 'bignumber.js';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
  CalculationSourceStreamEmission,
} from 'pmrv-api';

import {
  buildParameterCalculationMethodData,
  buildTaskData,
  maxIntegerPartValidator,
} from '../../calculation-emissions';
import { parametersOptionsMap } from '../../calculation-emissions-parameters';
import { NationalInventoryService } from '../../services/national-inventory.service';
import { RegionalInventoryService } from '../../services/regional-inventory.service';
import {
  calculateActivityData,
  getActivityCalculationNextStep,
  getActivityDataMeasuremenUnits,
  getFormData,
} from '../activity-calculation';

@Component({
  selector: 'app-activity-calculation-aggregation',
  templateUrl: './activity-calculation-aggregation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ActivityCalculationAggregationComponent implements OnInit {
  measurementUnits = getActivityDataMeasuremenUnits();

  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  isEditable$ = this.aerService.isEditable$;
  payload$ = this.aerService.getPayload();

  sourceStreamEmission$: Observable<CalculationSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    map(
      ([payload, index]) =>
        (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions?.[
          index
        ],
    ),
  );

  nationalInventoryData$ = this.nationalInventoryService.nationalInventoryData$;

  regionalInventoryData$ = this.sourceStreamEmission$.pipe(
    map((sourceStreamEmission) => {
      return (sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod)
        ?.localZoneCode;
    }),
    switchMap((localZoneCode) => this.regionalInventoryService.getRegionalInventoryData(localZoneCode)),
  );

  totalMaterial$: Observable<BigNumber> = of(new BigNumber(0));
  measurementUnit$: Observable<string> = of('');

  form$: Observable<UntypedFormGroup> = combineLatest([
    this.sourceStreamEmission$,
    this.nationalInventoryData$,
    this.regionalInventoryData$,
    this.isEditable$,
  ]).pipe(
    map(([sourceStreamEmission, nationalInventoryData, regionalInventoryData, isEditable]) => {
      const [predefinedMeasurementUnit, calculationActivityDataCalculationMethod] = getFormData(
        sourceStreamEmission,
        nationalInventoryData,
        regionalInventoryData,
      );

      const formControls = this.getFormControls(
        predefinedMeasurementUnit,
        calculationActivityDataCalculationMethod,
        isEditable,
      );

      return this.fb.group(formControls, {
        updateOn: 'submit',
      });
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

  ngOnInit(): void {
    this.setTotalMaterial();
  }

  onContinue(): void {
    combineLatest([this.form$, this.payload$, this.index$, this.totalMaterial$, this.regionalInventoryData$])
      .pipe(
        first(),
        filter(([form]) => form.valid),
        switchMap(([form, payload, index, totalMaterial, regionalInventoryData]) =>
          this.aerService.postTaskSave(
            this.buildData(form, payload, index, totalMaterial, regionalInventoryData),
            undefined,
            getCompletionStatus('CALCULATION_CO2', payload, index, false),
            'CALCULATION_CO2',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private buildData(
    form,
    payload: AerApplicationSubmitRequestTaskPayload,
    index: number,
    totalMaterial: BigNumber,
    regionalInventoryData,
  ) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;
    const sourceStreamEmission = calculation.sourceStreamEmissions?.[index];

    const materialImportedOrExported = form.get('materialImportedOrExported').value;

    const activityData = calculateActivityData(
      sourceStreamEmission,
      totalMaterial,
      regionalInventoryData?.calculationFactor,
    );

    const parameterCalculationMethodData = {
      calculationActivityDataCalculationMethod: {
        ...calculation.sourceStreamEmissions?.[index]?.parameterCalculationMethod
          ?.calculationActivityDataCalculationMethod,
        measurementUnit: form.get('measurementUnit').value,
        totalMaterial: totalMaterial.toString(),
        activityData,
        materialOpeningQuantity: form.get('materialOpeningQuantity').value,
        materialClosingQuantity: form.get('materialClosingQuantity').value,
        materialImportedOrExported,
        ...(materialImportedOrExported === true
          ? {
              materialImportedQuantity: form.get('materialImportedQuantity').value,
              materialExportedQuantity: form.get('materialExportedQuantity').value,
            }
          : {}),
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

    return buildTaskData(payload, sourceStreamEmissions);
  }

  private navigateNext() {
    this.sourceStreamEmission$.pipe(takeUntil(this.destroy$)).subscribe((sourceStreamEmission) => {
      const nextStep = getActivityCalculationNextStep(sourceStreamEmission);

      this.router.navigate([`../${nextStep}`], { relativeTo: this.route }).then();
    });
  }

  private setTotalMaterial() {
    const getControlAsObservable = (controlName) =>
      this.form$.pipe(
        switchMap((form) => {
          const control = form.get(controlName);
          const initialValue = control.value;

          return control.valueChanges.pipe(startWith(initialValue), shareReplay(1));
        }),
      );

    this.totalMaterial$ = combineLatest([
      getControlAsObservable('materialOpeningQuantity'),
      getControlAsObservable('materialClosingQuantity'),
      getControlAsObservable('materialImportedOrExported'),
      getControlAsObservable('materialImportedQuantity'),
      getControlAsObservable('materialExportedQuantity'),
    ]).pipe(
      map(
        ([
          materialOpeningQuantity,
          materialClosingQuantity,
          materialImportedOrExported,
          materialImportedQuantity,
          materialExportedQuantity,
        ]) => {
          let total = new BigNumber(0);

          if (!isNaN(materialOpeningQuantity) && !isNaN(materialClosingQuantity)) {
            total = new BigNumber(materialOpeningQuantity ?? 0).minus(materialClosingQuantity ?? 0);

            if (materialImportedOrExported && !isNaN(materialImportedQuantity) && !isNaN(materialExportedQuantity)) {
              total = total.plus(materialImportedQuantity ?? 0).minus(materialExportedQuantity ?? 0);
            }
          }
          return total;
        },
      ),
    );

    this.measurementUnit$ = combineLatest([getControlAsObservable('measurementUnit')]).pipe(
      map(
        ([measurementUnit]) =>
          parametersOptionsMap
            .find((option) => option.name === 'ACTIVITY_DATA')
            ?.measurementUnits.values.find((measurementUnitValue) => {
              return measurementUnitValue.value === measurementUnit;
            })?.text,
      ),
    );
  }

  private getFormControls(predefinedMeasurementUnit, calculationActivityDataCalculationMethod, isEditable) {
    return {
      measurementUnit: [
        {
          value:
            predefinedMeasurementUnit === 'GJ_PER_TONNE'
              ? 'TONNES'
              : predefinedMeasurementUnit === 'GJ_PER_NM3'
              ? 'NM3'
              : calculationActivityDataCalculationMethod?.measurementUnit
              ? calculationActivityDataCalculationMethod.measurementUnit
              : null,
          disabled: !isEditable || !!predefinedMeasurementUnit,
        },
        {
          validators: GovukValidators.required('Please select a measurement unit'),
          updateOn: 'change',
        },
      ],
      materialOpeningQuantity: [
        {
          value: calculationActivityDataCalculationMethod?.materialOpeningQuantity ?? null,
          disabled: !isEditable,
        },
        {
          validators: [
            GovukValidators.required('Please state the opening quantity of fuel'),
            maxIntegerPartValidator(),
          ],
          updateOn: 'change',
        },
      ],
      materialClosingQuantity: [
        {
          value: calculationActivityDataCalculationMethod?.materialClosingQuantity ?? null,
          disabled: !isEditable,
        },
        {
          validators: [
            GovukValidators.required('Please state the closing quantity of fuel'),
            maxIntegerPartValidator(),
          ],
          updateOn: 'change',
        },
      ],
      materialImportedOrExported: [
        {
          value: calculationActivityDataCalculationMethod?.materialImportedOrExported ?? null,
          disabled: !isEditable,
        },
        {
          validators: GovukValidators.required('Select yes or no'),
          updateOn: 'change',
        },
      ],
      materialImportedQuantity: [
        {
          value: calculationActivityDataCalculationMethod?.materialImportedQuantity ?? null,
          disabled: !isEditable,
        },
        {
          validators: [
            GovukValidators.required('Please state the total amount of imported fuel'),
            maxIntegerPartValidator(),
          ],
          updateOn: 'change',
        },
      ],
      materialExportedQuantity: [
        {
          value: calculationActivityDataCalculationMethod?.materialExportedQuantity ?? null,
          disabled: !isEditable,
        },
        {
          validators: [
            GovukValidators.required('Please state the total amount of exported fuel'),
            maxIntegerPartValidator(),
          ],
          updateOn: 'change',
        },
      ],
    };
  }
}
