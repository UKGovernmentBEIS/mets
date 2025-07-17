import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, Observable, shareReplay, switchMap, withLatestFrom } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationSourceStreamEmission,
  EmissionsCalculationParamsDTO,
  ReportingDataService,
  ReportingService,
  SourceStream,
} from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { getBiomassFormControls, getBiomassFormValidators } from '../biomass-calculation/biomass-calculation.provider';
import { buildParameterCalculationMethodData, buildTaskData, maxIntegerPartValidator } from '../calculation-emissions';
import { parametersOptionsMap } from '../calculation-emissions-parameters';

@Component({
  selector: 'app-calculation-values',
  templateUrl: './manual-calculation-values.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManualCalculationValuesComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();
  isEditable$ = this.aerService.isEditable$;

  formElements = [];

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

  emissionsCalculationInfo$ = this.sourceStreamType$.pipe(
    switchMap((sourceStreamType) => this.aerService.getCalculationParameterTypes(sourceStreamType)),
  );

  form$ = combineLatest([this.sourceStreamEmission$, this.emissionsCalculationInfo$, this.isEditable$]).pipe(
    map(([sourceStreamEmission, emissionsCalculationInfo, isEditable]) => {
      const parameters = sourceStreamEmission?.parameterMonitoringTiers;

      this.formElements = this.getFormElements(sourceStreamEmission, parameters, emissionsCalculationInfo);
      const [formControls, formValidators] = this.getFormControlsAndValidators(sourceStreamEmission, isEditable);

      return this.fb.group(formControls, formValidators);
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    private readonly fb: UntypedFormBuilder,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    readonly reportingService: ReportingService,
    readonly reportingDataService: ReportingDataService,
  ) {}

  onContinue(): void {
    combineLatest([this.form$, this.sourceStreamEmission$, this.sourceStreamType$])
      .pipe(
        first(),
        filter(([form]) => form.valid),
        switchMap(([form, sourceStreamEmission, sourceStreamType]) => {
          const { calculationActivityDataCalculationMethod } = sourceStreamEmission?.parameterCalculationMethod ?? {};
          const containsBiomass = sourceStreamEmission?.biomassPercentages?.contains;

          const manualEmissionsParams = this.getManualParametersValues(form, ['nonSustainableBiomassPercentage']);

          const emissionsCalculationParams: EmissionsCalculationParamsDTO = {
            activityData: calculationActivityDataCalculationMethod?.totalMaterial,
            activityDataMeasurementUnit: calculationActivityDataCalculationMethod?.measurementUnit,
            containsBiomass: containsBiomass,
            sourceStreamType: sourceStreamType,
            ...manualEmissionsParams,
          };

          return this.reportingService.calculateEmissions(emissionsCalculationParams);
        }),
        withLatestFrom(this.form$, this.payload$, this.index$),
        switchMap(([totals, form, payload, index]) =>
          this.aerService.postTaskSave(
            this.buildData(totals, form, payload, index),
            undefined,
            getCompletionStatus('CALCULATION_CO2', payload, index, false),
            'CALCULATION_CO2',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private buildData(totals, form, payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

    const manualEmissionsParams = this.getManualParametersValues(form, [
      'biomassPercentage',
      'nonSustainableBiomassPercentage',
    ]);

    const emissionCalculationParamValuesData = {
      ...manualEmissionsParams,
      ...{ totalReportableEmissions: totals?.reportableEmissions },
      ...(totals?.sustainableBiomassEmissions
        ? { totalSustainableBiomassEmissions: totals.sustainableBiomassEmissions }
        : {}),
    };

    const parameterCalculationMethod = buildParameterCalculationMethodData(
      calculation.sourceStreamEmissions?.[index],
      null,
      emissionCalculationParamValuesData,
    );

    const biomass = form.contains('biomassPercentage')
      ? {
          biomassPercentages: {
            contains: true,
            biomassPercentage: form.controls.biomassPercentage.value,
            nonSustainableBiomassPercentage: form.controls.nonSustainableBiomassPercentage.value,
          },
        }
      : {};

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...parameterCalculationMethod,
            ...biomass,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    const nextStep = '../manual-data-review';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }

  private getFormElements(sourceStreamEmission, parameters, emissionsCalculationInfo) {
    const activityDataMeasurementUnit =
      sourceStreamEmission?.parameterCalculationMethod?.calculationActivityDataCalculationMethod?.measurementUnit;

    const formElements = parameters
      .filter((parameter) => parameter.type !== 'BIOMASS_FRACTION' && parameter.type !== 'ACTIVITY_DATA')
      .map((parameter) => {
        const controlElement = parametersOptionsMap.find(
          (parameterOptions) => parameter.type === parameterOptions.name,
        );

        let measurementUnits;
        const measurementUnitName = controlElement?.measurementUnits?.name;
        if (measurementUnitName) {
          const measurementUnitsCombinations = emissionsCalculationInfo?.measurementUnitsCombinations
            ?.filter((measurementUnitsCombination) => {
              return measurementUnitsCombination.activityDataMeasurementUnit === activityDataMeasurementUnit;
            })
            .map((combination) => combination?.[measurementUnitName]);

          const measurementUnitValues = controlElement?.measurementUnits?.values.filter((unitValue) => {
            return measurementUnitsCombinations.includes(unitValue.value);
          });
          measurementUnits = { name: measurementUnitName, values: measurementUnitValues };
        }

        return {
          name: controlElement.name,
          emissionParameterName: controlElement.emissionParameterName,
          label: controlElement.label,
          measurementUnits: measurementUnits,
        };
      });
    return formElements;
  }

  private getFormControlsAndValidators(sourceStreamEmission, isEditable) {
    let formControlsAndValidators;

    const formControls = [];

    const emissionCalculationParamValues =
      sourceStreamEmission?.parameterCalculationMethod?.emissionCalculationParamValues;

    this.formElements.forEach((formElement) => {
      formControls[`${formElement.emissionParameterName}`] = [
        {
          value: emissionCalculationParamValues?.[formElement.emissionParameterName] ?? null,
          disabled: !isEditable,
        },
        [GovukValidators.required('Enter a value'), maxIntegerPartValidator()],
      ];

      if (formElement?.measurementUnits?.name) {
        const measurementUnitsName = formElement?.measurementUnits?.name;

        let value, disabled;
        if (measurementUnitsName === 'ncvMeasurementUnit') {
          const activityDataMeasurementUnit =
            sourceStreamEmission?.parameterCalculationMethod?.calculationActivityDataCalculationMethod?.measurementUnit;

          value = activityDataMeasurementUnit === 'TONNES' ? 'GJ_PER_TONNE' : 'GJ_PER_NM3';
          disabled = true;
        } else {
          value = emissionCalculationParamValues?.[formElement.measurementUnits.name] ?? null;
          disabled = !isEditable;
        }

        formControls[`${formElement.measurementUnits.name}`] = [
          {
            value,
            disabled,
          },
          GovukValidators.required('Please select a measurement unit'),
        ];
      }
    });

    if (sourceStreamEmission?.biomassPercentages?.contains) {
      const formControlsBiomass = {
        ...getBiomassFormControls(sourceStreamEmission, !isEditable),
      };
      const formValidatorsBiomass = {
        ...getBiomassFormValidators(),
      };

      formControlsAndValidators = [{ ...formControls, ...formControlsBiomass }, { ...formValidatorsBiomass }];
    } else {
      formControlsAndValidators = [{ ...formControls }, null];
    }

    return formControlsAndValidators;
  }

  private getManualParametersValues(form, excludeParameters = []) {
    const manualEmissionsParams = {};
    Object.keys(form.controls)
      .filter((control) => !['validators', ...excludeParameters].includes(control))
      .forEach((key) => {
        manualEmissionsParams[key] = form.controls[key].value;
      });
    return manualEmissionsParams;
  }
}
