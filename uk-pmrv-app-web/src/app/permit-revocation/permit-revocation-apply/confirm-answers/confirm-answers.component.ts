import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import {
  effectiveDateMinValidator,
  feeDateMinValidator,
  genericMinDateValidator,
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
  stoppedDateMaxDateValidator,
} from '@permit-revocation/factory/permit-revocation-form-provider';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { BackLinkService } from '@shared/back-link/back-link.service';

@Component({
  selector: 'app-confirm-answers',
  templateUrl: './confirm-answers.component.html',
  providers: [permitRevocationFormProvider, DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmAnswersComponent implements OnInit {
  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitRevocationStore,
    readonly route: ActivatedRoute,
    readonly router: Router,
    private readonly pendingRequest: PendingRequestService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        first(),
        tap((state) => {
          this.form.addValidators([effectiveDateMinValidator(), stoppedDateMaxDateValidator()]);

          if (state.permitRevocation?.feeDate) {
            this.form.addValidators(feeDateMinValidator());
          }
          if (state.permitRevocation?.annualEmissionsReportDate) {
            this.form.addValidators(genericMinDateValidator('annualEmissionsReportDate'));
          }
          if (state.permitRevocation?.surrenderDate) {
            this.form.addValidators(genericMinDateValidator('surrenderDate'));
          }
        }),
      )
      .subscribe();
  }

  confirm() {
    this.form.updateValueAndValidity();
    if (this.form.valid) {
      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) =>
            this.store.postApplyPermitRevocation({
              ...state,
              sectionsCompleted: {
                ...state.sectionsCompleted,
                [data.statusKey]: true,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(['..', 'summary'], { relativeTo: this.route, state: { notification: true } }),
        );
    }
  }
}
