import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { BehaviorSubject, first, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { GovukValidators } from 'govuk-components';

import { trackShouldDisplayCancelHintInfo } from '../../core/payment.map';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-cancel',
  templateUrl: './cancel.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class CancelComponent implements OnInit {
  readonly shouldDisplayCancelHintInfo$ = this.store.pipe(
    map((state) => trackShouldDisplayCancelHintInfo(state.requestTaskItem?.requestInfo?.type)),
  );

  confirmed$ = new BehaviorSubject<boolean>(false);
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  form: UntypedFormGroup;

  constructor(
    readonly store: PaymentStore,
    private readonly fb: UntypedFormBuilder,
    private readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.store.pipe(first()).subscribe((state) => {
      this.form = this.fb.group({
        reason: [
          { value: null, disabled: !state.isEditable },
          {
            validators: [
              GovukValidators.required('Enter the reason that no payment is required'),
              GovukValidators.maxLength(10000, 'The no payment reason should not be more than 10000 characters'),
            ],
          },
        ],
      });
    });
  }

  submitForm(): void {
    if (this.form.valid) {
      this.store
        .postTrackPaymentCancel({ ...this.form.value })
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.confirmed$.next(true);
          this.breadcrumbService.showDashboardBreadcrumb(this.router.url);
        });
    } else {
      this.isSummaryDisplayed$.next(true);
      this.form.markAllAsTouched();
    }
  }
}
