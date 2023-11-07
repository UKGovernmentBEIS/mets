import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { AerApplicationSubmitRequestTaskPayload, ParameterMonitoringTierDiffReason } from 'pmrv-api';

import { AerService } from '../../../../core/aer.service';
import { AER_APPROACHES_FORM, areCalculationsTiersExtraConditionsMet, buildTaskData } from '../emissions';
import { getCompletionStatus } from '../emissions-status';
import { tiersReasonFormProvider } from './tiers-reason.provider';

@Component({
  selector: 'app-tiers-reason',
  templateUrl: './tiers-reason.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [tiersReasonFormProvider, DestroySubject],
})
export class TiersReasonComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  taskKey = this.route.snapshot.data.taskKey;

  parameterMonitoringTiers$ = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      return (payload.aer.monitoringApproachEmissions[this.taskKey] as any)?.sourceStreamEmissions?.[index]
        ?.parameterMonitoringTiers;
    }),
  );

  usersToNotify$ = this.payload$.pipe(
    map((payload) => {
      return payload.permitOriginatedData.permitNotificationIds;
    }),
  );

  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_APPROACHES_FORM) readonly form: UntypedFormGroup,
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
      combineLatest([this.payload$, this.index$])
        .pipe(
          first(),
          switchMap(([payload, index]) =>
            this.aerService.postTaskSave(
              this.buildData(payload, index),
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

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const approach = monitoringApproachEmissions[this.taskKey] as any;

    const type = this.form.get('type').value;

    const parameterMonitoringTierDiffReason: ParameterMonitoringTierDiffReason = {
      reason: type === 'DATA_GAP' ? this.form.get('reasonDataGap').value : this.form.get('reasonOther').value,
      type: type,
      ...(type === 'DATA_GAP' ? { relatedNotifications: this.form.get('relatedNotifications').value } : {}),
    };

    const sourceStreamEmissions = approach.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            parameterMonitoringTierDiffReason,
          }
        : item,
    );

    return buildTaskData(this.taskKey, payload, sourceStreamEmissions);
  }

  private navigateNext() {
    this.parameterMonitoringTiers$.pipe(takeUntil(this.destroy$)).subscribe((parameterMonitoringTiers) => {
      let nextStep = '.';

      switch (this.taskKey) {
        case 'CALCULATION_CO2': {
          if (areCalculationsTiersExtraConditionsMet(parameterMonitoringTiers)) {
            nextStep = '../calculation-method';
          } else {
            nextStep = '../activity-calculation-method';
          }
          break;
        }
        case 'CALCULATION_PFC': {
          nextStep = '../primary-aluminium';
          break;
        }
      }

      this.router.navigate([`${nextStep}`], { relativeTo: this.route }).then();
    });
  }
}
