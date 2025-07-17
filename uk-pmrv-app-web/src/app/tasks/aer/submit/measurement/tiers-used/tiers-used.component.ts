import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../../core/aer.service';
import { buildTaskData } from '../measurement';
import { AER_MEASUREMENT_FORM, getCompletionStatus } from '../measurement-status';
import { tiersUsedFormProvider } from './tiers-used-forn.provider';

@Component({
  selector: 'app-tiers-used',
  templateUrl: './tiers-used.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [tiersUsedFormProvider, DestroySubject],
})
export class TiersUsedComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  payload$ = this.aerService.getPayload();

  taskKey = this.route.snapshot.data.taskKey;
  isEditable$ = this.aerService.isEditable$;

  emissionPointEmission$ = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      return (payload.aer.monitoringApproachEmissions?.[this.taskKey] as any)?.emissionPointEmissions?.[index];
    }),
  );

  permitParamMonitoringTier$ = combineLatest([this.payload$, this.emissionPointEmission$]).pipe(
    map(([payload, emissionPointEmission]) => {
      const emissionPointEmissionId = emissionPointEmission?.id;
      const emissionPointParamMonitoringTiers =
        this.taskKey === 'MEASUREMENT_CO2'
          ? 'measurementCO2EmissionPointParamMonitoringTiers'
          : 'measurementN2OEmissionPointParamMonitoringTiers';

      return emissionPointEmissionId
        ? payload.permitOriginatedData.permitMonitoringApproachMonitoringTiers?.[emissionPointParamMonitoringTiers]?.[
            emissionPointEmissionId
          ]
        : [];
    }),
  );

  constructor(
    @Inject(AER_MEASUREMENT_FORM) readonly form: UntypedFormGroup,
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
              getCompletionStatus(this.taskKey, payload, index, false),
              this.taskKey,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, permitParamMonitoringTier, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const measurement = monitoringApproachEmissions[this.taskKey] as any;

    const tier = this.form.get('tier').value;

    const doAerTiersMatchPermitTiers = tier === permitParamMonitoringTier;

    const emissionPointEmissions = measurement.emissionPointEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            tier,
            ...(doAerTiersMatchPermitTiers ? { parameterMonitoringTierDiffReason: null } : null),
            calculationCorrect: null,
          }
        : item,
    );

    return buildTaskData(this.taskKey, payload, emissionPointEmissions);
  }

  private navigateNext() {
    combineLatest([this.emissionPointEmission$, this.permitParamMonitoringTier$])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([emissionPointEmission, permitParamMonitoringTier]) => {
        let nextStep = '';

        const doAerTiersMatchPermitTiers = emissionPointEmission?.tier === permitParamMonitoringTier;
        const containsBiomass = !!emissionPointEmission?.biomassPercentages?.contains;

        if (!doAerTiersMatchPermitTiers) {
          nextStep = '../tiers-reason';
        } else {
          if (containsBiomass) {
            nextStep = '../biomass-calculation';
          } else {
            nextStep = '../ghg-concentration';
          }
        }

        this.router.navigate([`${nextStep}`], { relativeTo: this.route });
      });
  }
}
