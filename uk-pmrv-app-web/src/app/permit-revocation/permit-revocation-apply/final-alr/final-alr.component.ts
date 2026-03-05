import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import {
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
} from '@permit-revocation/factory/permit-revocation-form-provider';
import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { SharedModule } from '@shared/shared.module';
import { startOfDay } from 'date-fns';

import { PermitRevocation } from 'pmrv-api';

@Component({
  selector: 'app-revocation-final-alr',
  standalone: true,
  imports: [SharedModule, RouterLink],
  templateUrl: './final-alr.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [permitRevocationFormProvider],
})
export class RevocationFinalAlrComponent {
  today = startOfDay(new Date());

  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitRevocationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../surrender-allowances'], { relativeTo: this.route });
    } else {
      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) => {
            const { alrRequired, alrReportDate } = this.form.value as Pick<
              PermitRevocation,
              'alrRequired' | 'alrReportDate'
            >;

            const permitRevocation: PermitRevocationState = {
              ...state,
              permitRevocation: {
                ...state.permitRevocation,
                alrRequired,
                alrReportDate: alrRequired ? alrReportDate : null,
              },
              sectionsCompleted: {
                [data.statusKey]: false,
              },
            };
            return this.store.postApplyPermitRevocation(permitRevocation);
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['..', 'surrender-allowances'], { relativeTo: this.route }));
    }
  }
}
