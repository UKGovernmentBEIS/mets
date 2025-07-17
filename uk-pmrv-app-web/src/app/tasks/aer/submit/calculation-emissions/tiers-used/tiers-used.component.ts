import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, iif, map, Observable, of, shareReplay, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationParameterCalculationMethod,
  CalculationSourceStreamEmission,
} from 'pmrv-api';

import { AerService } from '../../../core/aer.service';
import { notCalculationPrimaryPFCError } from '../../../error/business-errors';
import { areCalculationsTiersExtraConditionsMet } from '../../../shared/components/submit/emissions';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { buildTaskData } from '../calculation-emissions';
import { parametersOptionsMap } from '../calculation-emissions-parameters';

@Component({
  selector: 'app-tiers-used',
  templateUrl: './tiers-used.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class TiersUsedComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  isEditable$ = this.aerService.isEditable$;
  payload$ = this.aerService.getPayload();

  formElements = [];

  sourceStreamEmission$: Observable<CalculationSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      return (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
        ?.sourceStreamEmissions?.[index];
    }),
  );

  calculationParameterTypes$ = combineLatest([this.payload$, this.sourceStreamEmission$]).pipe(
    map(([payload, sourceStreamEmission]) => {
      const usedSourceStream = payload.aer.sourceStreams.find(
        (sourceStream) => sourceStream.id === sourceStreamEmission.sourceStream,
      );
      return usedSourceStream?.type;
    }),
    switchMap((sourceStreamType) => this.aerService.getCalculationParameterTypes(sourceStreamType)),
    catchBadRequest(ErrorCodes.AER1008, () =>
      this.businessErrorService.showError(notCalculationPrimaryPFCError(this.aerService.requestTaskId)),
    ),
    map((calculationParameterTypes) => {
      return calculationParameterTypes?.applicableTypes;
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  permitParamMonitoringTiers$ = combineLatest([this.payload$, this.sourceStreamEmission$]).pipe(
    map(([payload, sourceStreamEmission]) => {
      const sourceStreamEmissionId = sourceStreamEmission?.id;
      return sourceStreamEmissionId
        ? payload.permitOriginatedData.permitMonitoringApproachMonitoringTiers
            .calculationSourceStreamParamMonitoringTiers[sourceStreamEmissionId]
        : [];
    }),
  );

  calculationParameters$ = this.sourceStreamEmission$.pipe(
    switchMap((sourceStreamEmission) =>
      iif(
        () => !!sourceStreamEmission?.parameterMonitoringTiers?.length,
        of(sourceStreamEmission?.parameterMonitoringTiers),
        this.calculationParameterTypes$.pipe(
          map((calculationParameterTypes) => calculationParameterTypes.map((parameter) => ({ type: parameter }))),
        ),
      ),
    ),
  );

  form$ = combineLatest([
    this.sourceStreamEmission$,
    this.calculationParameters$,
    this.permitParamMonitoringTiers$,
    this.isEditable$,
  ]).pipe(
    map(([sourceStreamEmission, calculationParameters, permitParamMonitoringTiers, isEditable]) => {
      this.formElements = this.getFormElements(
        calculationParameters,
        sourceStreamEmission?.biomassPercentages?.contains,
      );

      this.setPermitTiers(permitParamMonitoringTiers);

      const formControls = this.getFormControls(isEditable);

      return this.fb.group(formControls);
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    private readonly fb: UntypedFormBuilder,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    private readonly destroy$: DestroySubject,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  setPermitTiers(permitParamMonitoringTiers) {
    const result = [];

    this.formElements.forEach((formElement) => {
      const permitTier = permitParamMonitoringTiers.find((permitParamMonitoringTier) => {
        return permitParamMonitoringTier.type === formElement.type;
      })?.tier;

      result.push({ ...formElement, ...{ permitTier: permitTier } });
    });

    this.formElements = result;
  }

  getFormElements(parameters, containsBiomass) {
    const formElements = parameters
      .filter((parameter) => parameter.type !== 'BIOMASS_FRACTION')
      .map((parameter) => {
        const options = parametersOptionsMap.find((parameterOptions) => parameter.type === parameterOptions.name);

        return { ...parameter, ...options };
      });

    if (containsBiomass) {
      const parameter = parameters.find((parameter) => parameter.type === 'BIOMASS_FRACTION');
      const options = parametersOptionsMap.find((parameterOptions) => parameterOptions.name === 'BIOMASS_FRACTION');

      formElements.push({ ...parameter, ...options });
    }

    return formElements;
  }

  getFormControls(isEditable) {
    const controls = [];

    this.formElements.forEach((parameter) => {
      controls[`${parameter.name}`] = [
        {
          value: parameter?.tier ?? null,
          disabled: !isEditable,
        },
        GovukValidators.required('You must select a suitable tier'),
      ];
    });

    return controls;
  }

  onContinue(): void {
    combineLatest([this.form$, this.permitParamMonitoringTiers$, this.payload$, this.index$])
      .pipe(
        first(),
        filter(([form]) => form.valid),
        switchMap(([form, permitParamMonitoringTiers, payload, index]) =>
          this.aerService.postTaskSave(
            this.buildData(form, permitParamMonitoringTiers, payload, index),
            undefined,
            getCompletionStatus('CALCULATION_CO2', payload, index, false),
            'CALCULATION_CO2',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private buildData(form, permitParamMonitoringTiers, payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;
    const sourceStreamEmission = calculation.sourceStreamEmissions?.[index];

    const parameterMonitoringTiers = Object.keys(form.value).map((key) => {
      return { type: key, tier: form.value[key] };
    });

    const emissionCalculationParamValues = (sourceStreamEmission?.parameterCalculationMethod as any)
      ?.emissionCalculationParamValues;

    const calculationActivityDataCalculationMethod =
      sourceStreamEmission?.parameterCalculationMethod?.calculationActivityDataCalculationMethod;

    const tiersExtraConditionsMet = areCalculationsTiersExtraConditionsMet(parameterMonitoringTiers);
    const previousCalculationMethodType = sourceStreamEmission?.parameterCalculationMethod?.type;

    let newCalculationMethodType: CalculationParameterCalculationMethod['type'];

    if (!previousCalculationMethodType) {
      if (tiersExtraConditionsMet) {
        newCalculationMethodType = null;
      } else {
        newCalculationMethodType = 'MANUAL';
      }
    } else if (
      previousCalculationMethodType === 'REGIONAL_DATA' ||
      previousCalculationMethodType === 'NATIONAL_INVENTORY_DATA'
    ) {
      newCalculationMethodType = !tiersExtraConditionsMet ? 'MANUAL' : previousCalculationMethodType;
    } else if (previousCalculationMethodType === 'MANUAL') {
      newCalculationMethodType = 'MANUAL';
    }

    const parameterCalculationMethod = {
      ...(newCalculationMethodType ? { type: newCalculationMethodType } : {}),
      ...(emissionCalculationParamValues &&
      newCalculationMethodType === 'MANUAL' &&
      previousCalculationMethodType === 'MANUAL'
        ? {
            emissionCalculationParamValues: {
              ...emissionCalculationParamValues,
              totalReportableEmissions: null,
              totalSustainableBiomassEmissions: null,
              calculationCorrect: null,
              providedEmissions: null,
            },
          }
        : {}),
      ...(calculationActivityDataCalculationMethod
        ? {
            calculationActivityDataCalculationMethod: calculationActivityDataCalculationMethod,
          }
        : {}),
    };

    let sourceStreamEmissionPayload = {
      ...sourceStreamEmission,
      parameterMonitoringTiers,
      ...(parameterCalculationMethod && Object.keys(parameterCalculationMethod).length
        ? { parameterCalculationMethod: parameterCalculationMethod }
        : {}),
    };

    if (this.doAerTiersMatchPermitTiers(sourceStreamEmissionPayload, permitParamMonitoringTiers)) {
      sourceStreamEmissionPayload = Object.keys(sourceStreamEmissionPayload)
        .filter((key) => key !== 'parameterMonitoringTierDiffReason')
        .reduce((result, key) => ({ ...result, [key]: sourceStreamEmissionPayload[key] }), {}) as any;
    }

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...sourceStreamEmissionPayload,
          }
        : item,
    );

    return buildTaskData(payload, sourceStreamEmissions);
  }

  private navigateNext() {
    combineLatest([this.sourceStreamEmission$, this.permitParamMonitoringTiers$])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([sourceStreamEmission, permitParamMonitoringTiers]) => {
        let nextStep = '';

        const tiersExtraConditionsMet = areCalculationsTiersExtraConditionsMet(
          sourceStreamEmission.parameterMonitoringTiers,
        );

        const aerTiersMatchPermitTiers = this.doAerTiersMatchPermitTiers(
          sourceStreamEmission,
          permitParamMonitoringTiers,
        );

        if (!aerTiersMatchPermitTiers) {
          nextStep = '../tiers-reason';
        } else {
          if (tiersExtraConditionsMet) {
            nextStep = '../calculation-method';
          } else {
            nextStep = '../activity-calculation-method';
          }
        }

        this.router.navigate([`${nextStep}`], { relativeTo: this.route });
      });
  }

  private doAerTiersMatchPermitTiers(sourceStreamEmission, permitParamMonitoringTiers) {
    const stringifiedSourceStreamEmissionTiers = JSON.stringify(
      this.sortTiersByType(sourceStreamEmission?.parameterMonitoringTiers),
    );
    const stringifiedPermitParamMonitoringTiers = JSON.stringify(this.sortTiersByType(permitParamMonitoringTiers));

    return stringifiedSourceStreamEmissionTiers === stringifiedPermitParamMonitoringTiers;
  }

  private sortTiersByType(arr) {
    return arr.sort((a, b) => a.type.localeCompare(b.type));
  }
}
