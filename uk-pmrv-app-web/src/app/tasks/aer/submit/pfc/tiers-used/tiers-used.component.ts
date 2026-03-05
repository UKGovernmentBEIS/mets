import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions, PfcSourceStreamEmission } from 'pmrv-api';

import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { AER_PFC_FORM, buildTaskData } from '../pfc';
import { tiersUsedFormProvider } from './tiers-used-form.provider';

@Component({
  selector: 'app-tiers-used',
  templateUrl: './tiers-used.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [tiersUsedFormProvider, DestroySubject],
})
export class TiersUsedComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();
  isEditable$ = this.aerService.isEditable$;

  sourceStreamEmission$: Observable<PfcSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      return (payload.aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions)
        ?.sourceStreamEmissions?.[index];
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
    private readonly destroy$: DestroySubject,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.navigateNext();
    } else {
      combineLatest([this.payload$, this.permitParamMonitoringTier$, this.index$])
        .pipe(
          first(),
          switchMap(([payload, permitParamMonitoringTier, index]) =>
            this.aerService.postTaskSave(
              this.buildData(payload, permitParamMonitoringTier, index),
              undefined,
              getCompletionStatus('CALCULATION_PFC', payload, index, false),
              'CALCULATION_PFC',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, permitParamMonitoringTier, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const pfc = monitoringApproachEmissions['CALCULATION_PFC'] as any;

    const sourceStreamEmission = pfc?.sourceStreamEmissions?.[index];

    const formParameterMonitoringTier = {
      emissionFactorTier: this.form.get('emissionFactorTier').value,
      activityDataTier: this.form.get('activityDataTier').value,
    };

    const sourceStreamEmissions = pfc.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...{ parameterMonitoringTier: formParameterMonitoringTier },
            ...(this.doAerTiersMatchPermitTiers(permitParamMonitoringTier) &&
            this.doMassBalanceMatchPermitMassBalance(sourceStreamEmission, permitParamMonitoringTier)
              ? { parameterMonitoringTierDiffReason: null }
              : null),
            calculationCorrect: null,
          }
        : item,
    );

    return buildTaskData(payload, sourceStreamEmissions);
  }

  private navigateNext() {
    combineLatest([this.sourceStreamEmission$, this.permitParamMonitoringTier$])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([sourceStreamEmission, permitParamMonitoringTier]) => {
        let nextStep = '';

        if (
          !this.doAerTiersMatchPermitTiers(permitParamMonitoringTier) ||
          !this.doMassBalanceMatchPermitMassBalance(sourceStreamEmission, permitParamMonitoringTier)
        ) {
          nextStep = '../tiers-reason';
        } else {
          nextStep = '../primary-aluminium';
        }

        this.router.navigate([`${nextStep}`], { relativeTo: this.route });
      });
  }

  private doAerTiersMatchPermitTiers(permitParamMonitoringTier) {
    return (
      permitParamMonitoringTier.emissionFactorTier === this.form.get('emissionFactorTier').value &&
      permitParamMonitoringTier.activityDataTier === this.form.get('activityDataTier').value
    );
  }

  private doMassBalanceMatchPermitMassBalance(sourceStreamEmission, permitParamMonitoringTier) {
    return permitParamMonitoringTier.massBalanceApproachUsed === sourceStreamEmission.massBalanceApproachUsed;
  }
}
