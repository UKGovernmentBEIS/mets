import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, take } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import {
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
} from '@permit-revocation/factory/permit-revocation-form-provider';
import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  providers: [permitRevocationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportComponent {
  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  isFinalAlrVisible = toSignal(this.store.isFinalAlrVisible$);

  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitRevocationStore,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue() {
    const navigate = (isFinalAlrVisible: boolean) =>
      isFinalAlrVisible
        ? this.router.navigate(['..', 'final-alr'], { relativeTo: this.route })
        : this.router.navigate(['..', 'surrender-allowances'], { relativeTo: this.route });

    if (!this.form.dirty) {
      const isFinalAlrVisible = this.isFinalAlrVisible();
      navigate(isFinalAlrVisible);
    } else {
      const annualEmissionsReportRequired = this.form.value.annualEmissionsReportRequired;
      const annualEmissionsReportDate = annualEmissionsReportRequired
        ? this.form.value.annualEmissionsReportDate
        : null;
      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) => {
            const permitRevocation: PermitRevocationState = {
              ...state,
              permitRevocation: {
                ...state.permitRevocation,
                annualEmissionsReportRequired,
                annualEmissionsReportDate,
              },
              sectionsCompleted: {
                [data.statusKey]: false,
              },
            };
            return this.store.postApplyPermitRevocation(permitRevocation);
          }),
          this.pendingRequest.trackRequest(),
        )
        .pipe(
          switchMap(() => this.store.isFinalAlrVisible$),
          take(1),
        )
        .subscribe((isFinalAlrVisible) => navigate(isFinalAlrVisible));
    }
  }
}
