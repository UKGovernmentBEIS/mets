import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { MeasurementOfCO2EmissionPointCategoryAppliedTier } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { MEASUREMENTCategoryTierSubtaskStatus } from '../../measurement-status';
import { appliedStandardFormProvider } from './applied-standard-form.provider';

@Component({
  selector: 'app-applied-standard',
  templateUrl: './applied-standard.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [appliedStandardFormProvider],
})
export class AppliedStandardComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  cannotStartYet$ = combineLatest([this.route.data, this.index$, this.store]).pipe(
    map(
      ([data, index, state]) =>
        MEASUREMENTCategoryTierSubtaskStatus(state, data.statusKey, index) === 'cannot start yet',
    ),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    combineLatest([this.route.data, this.index$, this.store])
      .pipe(
        first(),
        switchMap(([data, tierIndex, state]) =>
          this.store.findTask<MeasurementOfCO2EmissionPointCategoryAppliedTier[]>(data.taskKey).pipe(
            first(),
            switchMap((tiers) =>
              this.store.postTask(
                data.taskKey,
                this.isNewAppliedStandard(tierIndex, tiers)
                  ? [...(tiers ?? []), { appliedStandard: this.form.value }]
                  : tiers.map((tier, index) =>
                      tierIndex === index ? { ...tier, appliedStandard: this.form.value } : tier,
                    ),
                state.permitSectionsCompleted.MEASUREMENT_CO2_Applied_Standard.map((item, idx) =>
                  tierIndex === idx ? true : item,
                ),
                data.statusKey,
              ),
            ),
            this.pendingRequest.trackRequest(),
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
  }

  private isNewAppliedStandard(tierIndex: number, tiers: MeasurementOfCO2EmissionPointCategoryAppliedTier[]) {
    return tierIndex === (tiers?.length || 0);
  }
}
