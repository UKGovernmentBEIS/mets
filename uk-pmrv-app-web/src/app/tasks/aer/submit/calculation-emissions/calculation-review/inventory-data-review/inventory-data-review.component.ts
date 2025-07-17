import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  filter,
  first,
  iif,
  map,
  Observable,
  of,
  shareReplay,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationNationalInventoryDataCalculationMethod,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
  CalculationSourceStreamEmission,
  EmissionsCalculationParamsDTO,
  ReportingService,
  SourceStream,
} from 'pmrv-api';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { AerService } from '../../../../core/aer.service';
import { getCompletionStatus } from '../../../../shared/components/submit/emissions-status';
import { buildParameterCalculationMethodData, buildTaskData } from '../../calculation-emissions';
import { NationalInventoryService } from '../../services/national-inventory.service';
import { RegionalInventoryService } from '../../services/regional-inventory.service';
import { getCalculationReviewFormControls, getEmissionsElements, isRegionalDataSelected } from '../calculation-reviews';

@Component({
  selector: 'app-inventory-data-review',
  templateUrl: './inventory-data-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InventoryDataReviewComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();
  isEditable$ = this.aerService.isEditable$;

  calculationFactor$ = new BehaviorSubject<string>(null);

  sourceStreamEmission$: Observable<CalculationSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      const res = (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
        ?.sourceStreamEmissions?.[index];
      return res;
    }),
  );

  sourceStreamType$: Observable<SourceStream['type']> = combineLatest([this.payload$, this.sourceStreamEmission$]).pipe(
    map(
      ([payload, sourceStreamEmission]) =>
        payload.aer.sourceStreams.find((sourceStream) => sourceStream.id === sourceStreamEmission.sourceStream)?.type,
    ),
  );

  nationalPrecalculatedEmissions$ = this.nationalInventoryService.nationalInventoryData$.pipe(
    withLatestFrom(this.sourceStreamEmission$),
    map(([nationalInventoryData, sourceStreamEmission]) => {
      const parameterCalculationMethod =
        sourceStreamEmission.parameterCalculationMethod as CalculationNationalInventoryDataCalculationMethod;

      const selectedSector = parameterCalculationMethod?.mainActivitySector;
      const selectedFuel = parameterCalculationMethod?.fuel;

      const precalculatedEmissions = nationalInventoryData.sectors
        .find((sector) => {
          return sector.name === selectedSector;
        })
        ?.fuels.find((fuel) => {
          return fuel.name === selectedFuel;
        })?.emissionCalculationParameters;

      return precalculatedEmissions;
    }),
  );

  regionalInventoryData$ = this.sourceStreamEmission$.pipe(
    switchMap((sourceStreamEmission) =>
      this.regionalInventoryService.getRegionalInventoryData(
        (sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod)?.localZoneCode,
      ),
    ),
  );

  regionalPrecalculatedEmissions$ = this.regionalInventoryData$.pipe(
    tap((regionalInventoryData) => {
      this.calculationFactor$.next(regionalInventoryData?.calculationFactor);
    }),
    map((regionalInventoryData) => {
      const precalculatedEmissions = Object.keys(regionalInventoryData)
        .filter((key) => key !== 'calculationFactor')
        .reduce((obj, key) => {
          obj[key] = regionalInventoryData[key];
          return obj;
        }, {});
      return precalculatedEmissions;
    }),
  );

  precalculatedEmissions$ = this.sourceStreamEmission$.pipe(
    switchMap((sourceStreamEmission) =>
      iif(
        () => isRegionalDataSelected(sourceStreamEmission),
        this.regionalPrecalculatedEmissions$,
        this.nationalPrecalculatedEmissions$,
      ),
    ),
  );

  calculatedEmissions$ = combineLatest([
    this.sourceStreamEmission$,
    this.sourceStreamType$,
    this.precalculatedEmissions$,
  ]).pipe(
    switchMap(([sourceStreamEmission, sourceStreamType, precalculatedEmissions]) => {
      const { calculationActivityDataCalculationMethod } = sourceStreamEmission?.parameterCalculationMethod ?? {};
      const containsBiomass = sourceStreamEmission?.biomassPercentages?.contains;

      const emissionsCalculationParams: EmissionsCalculationParamsDTO = {
        activityData: calculationActivityDataCalculationMethod?.activityData,
        activityDataMeasurementUnit: calculationActivityDataCalculationMethod?.measurementUnit,
        containsBiomass: containsBiomass,
        ...(containsBiomass ? { biomassPercentage: sourceStreamEmission?.biomassPercentages?.biomassPercentage } : {}),
        sourceStreamType: sourceStreamType,
        ...precalculatedEmissions,
      };

      return this.reportingService.calculateEmissions(emissionsCalculationParams).pipe(
        map((calculatedTotals) => {
          const totalValues = {
            ...{ totalReportableEmissions: calculatedTotals?.reportableEmissions },
            ...(calculatedTotals?.sustainableBiomassEmissions
              ? { totalSustainableBiomassEmissions: calculatedTotals.sustainableBiomassEmissions }
              : {}),
          };
          return {
            ...totalValues,
            ...precalculatedEmissions,
          };
        }),
      );
    }),
  );

  emissionsElements$ = combineLatest([
    this.sourceStreamEmission$,
    this.calculatedEmissions$,
    this.calculationFactor$,
  ]).pipe(
    map(([sourceStreamEmission, calculatedEmissions, calculationFactor]) => {
      return getEmissionsElements(sourceStreamEmission, calculatedEmissions, calculationFactor);
    }),
  );

  form$ = combineLatest([this.sourceStreamEmission$, this.isEditable$]).pipe(
    map(([sourceStreamEmission, isEditable]) => {
      const formControls = getCalculationReviewFormControls(sourceStreamEmission, !isEditable);
      return this.fb.group(formControls);
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    readonly reportingService: ReportingService,
    readonly nationalInventoryService: NationalInventoryService,
    readonly regionalInventoryService: RegionalInventoryService,
    private readonly fb: UntypedFormBuilder,
  ) {}

  onContinue(): void {
    combineLatest([this.form$, this.calculatedEmissions$, this.calculationFactor$, this.payload$, this.index$])
      .pipe(
        first(),
        filter(([form]) => form.valid),
        switchMap(([form, calculatedEmissions, calculationFactor, payload, index]) =>
          iif(
            () => form.dirty,
            this.aerService.postTaskSave(
              this.buildData(form, calculatedEmissions, calculationFactor, payload, index),
              undefined,
              getCompletionStatus('CALCULATION_CO2', payload, index, false),
              'CALCULATION_CO2',
            ),
            of(null),
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private buildData(
    form,
    calculatedEmissions,
    calculationFactor,
    payload: AerApplicationSubmitRequestTaskPayload,
    index: number,
  ) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;
    const sourceStreamEmission = calculation.sourceStreamEmissions?.[index];

    const calculationCorrect = form.controls.calculationCorrect.value;

    const emissionCalculationParamValuesData = {
      ...calculatedEmissions,
      ...(calculationFactor !== null ? { calculationFactor: calculationFactor } : {}),
      calculationCorrect: calculationCorrect,
      ...(!calculationCorrect
        ? {
            providedEmissions: {
              reasonForProvidingManualEmissions: form.controls.reasonForProvidingManualEmissions.value,
              totalProvidedReportableEmissions: form.controls.totalProvidedReportableEmissions.value,
              ...(sourceStreamEmission?.biomassPercentages?.contains
                ? {
                    totalProvidedSustainableBiomassEmissions:
                      form.controls.totalProvidedSustainableBiomassEmissions.value,
                  }
                : {}),
            },
          }
        : { providedEmissions: null }),
    };

    const parameterCalculationMethod = buildParameterCalculationMethodData(
      calculation.sourceStreamEmissions?.[index],
      null,
      emissionCalculationParamValuesData,
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
    const nextStep = '../summary';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }

  getFormControls(isEditable) {
    const formControls = {};

    formControls[`calculationCorrect`] = [
      {
        value: null,
        disabled: !isEditable,
      },
      GovukValidators.required('Please select a measurement unit'),
    ];
  }
}
