import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, Observable, switchMap } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions, PfcSourceStreamEmission } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { AER_PFC_FORM, buildTaskData } from '../pfc';
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

  sourceStreamEmission$: Observable<PfcSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    filter(([payload]) => !!payload),
    map(([payload, index]) => {
      const res = (payload.aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions)
        ?.sourceStreamEmissions?.[index];
      return res;
    }),
  );

  permitParamMonitoringTier$ = combineLatest([this.payload$, this.sourceStreamEmission$]).pipe(
    map(([payload, sourceStreamEmission]) => {
      const sourceStreamEmissionId = sourceStreamEmission?.id;

      return sourceStreamEmissionId
        ? payload.permitOriginatedData.permitMonitoringApproachMonitoringTiers
            .calculationPfcSourceStreamParamMonitoringTiers[sourceStreamEmissionId]
        : [];
    }),
  );

  constructor(
    @Inject(AER_PFC_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
    combineLatest([this.payload$, this.index$, this.permitParamMonitoringTier$])
      .pipe(
        first(),
        switchMap(([payload, index, permitParamMonitoringTier]) =>
          this.aerService.postTaskSave(
            this.buildData(payload, index, permitParamMonitoringTier),
            undefined,
            getCompletionStatus('CALCULATION_PFC', payload, index, false),
            'CALCULATION_PFC',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  onDelete() {
    const nextStep = '../delete';
    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number, permitParamMonitoringTier) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const pfc = monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions;

    const sourceStreamEmission = pfc?.sourceStreamEmissions?.[index];

    const formMassBalanceApproachUsed = this.form.get('massBalanceApproachUsed').value;

    const formData = {
      sourceStream: this.form.get('sourceStream').value,
      emissionSources: this.form.get('emissionSources').value,
      pfcSourceStreamEmissionCalculationMethodData: {
        ...(this.form.get('calculationMethod').value ===
        sourceStreamEmission?.pfcSourceStreamEmissionCalculationMethodData?.calculationMethod
          ? sourceStreamEmission?.pfcSourceStreamEmissionCalculationMethodData
          : {}),
        calculationMethod: this.form.get('calculationMethod').value,
      },
      massBalanceApproachUsed: formMassBalanceApproachUsed,
      parameterMonitoringTier: {
        ...sourceStreamEmission?.parameterMonitoringTier,
        ...(formMassBalanceApproachUsed === false &&
        ['TIER_4', 'TIER_3'].includes(sourceStreamEmission?.parameterMonitoringTier?.activityDataTier)
          ? { activityDataTier: null }
          : {}),
      },
      ...(formMassBalanceApproachUsed === permitParamMonitoringTier.massBalanceApproachUsed &&
      permitParamMonitoringTier.emissionFactorTier ===
        sourceStreamEmission?.parameterMonitoringTier?.emissionFactorTier &&
      permitParamMonitoringTier.activityDataTier === sourceStreamEmission?.parameterMonitoringTier?.activityDataTier
        ? { parameterMonitoringTierDiffReason: null }
        : {}),
    };

    const sourceStreamEmissionPayload = {
      ...sourceStreamEmission,
      ...formData,
      calculationCorrect: null,
    };

    const sourceStreamEmissions =
      pfc?.sourceStreamEmissions && pfc.sourceStreamEmissions?.[index]
        ? pfc.sourceStreamEmissions.map((item, idx) => {
            return idx === index
              ? {
                  ...sourceStreamEmissionPayload,
                }
              : item;
          })
        : [...(pfc?.sourceStreamEmissions ?? []), sourceStreamEmissionPayload];

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }
  private navigateNext() {
    const nextStep = 'date-range';

    this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
  }
}
