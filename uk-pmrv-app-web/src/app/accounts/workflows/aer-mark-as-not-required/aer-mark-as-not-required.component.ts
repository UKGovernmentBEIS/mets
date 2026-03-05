import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { RequestsService } from 'pmrv-api';

import { AER_NOT_REQUIRE_FORM, aerReasonProvider } from './aer-mark-as-not-required-form.provider';

@Component({
  selector: 'app-aer-mark-as-not-required',
  templateUrl: './aer-mark-as-not-required.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, aerReasonProvider],
})
export class AerMarkAsNotRequiredComponent {
  isMarkedAsNotRequired$ = new BehaviorSubject<boolean>(false);
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  reference$ = new BehaviorSubject<string>(null);
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  requestId$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('request-id')));

  constructor(
    @Inject(AER_NOT_REQUIRE_FORM) public form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly requestsService: RequestsService,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirmMarkAsNotRequired(): void {
    if (this.form.valid) {
      this.requestId$
        .pipe(
          first(),
          switchMap((requestId) => {
            this.reference$.next(requestId);
            return this.requestsService.markAsNotRequired(requestId, this.form.value);
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.isMarkedAsNotRequired$.next(true);
        });
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }
}
