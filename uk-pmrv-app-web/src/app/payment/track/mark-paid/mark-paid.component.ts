import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject, first, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import {
  getHeadingMap,
  mapTrackPaymentToPaymentDetails,
  trackShouldDisplayMarkPaidConfirmationInfo,
} from '../../core/payment.map';
import { getPaymentBaseLink, shouldHidePaymentAmount } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-mark-paid',
  templateUrl: './mark-paid.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class MarkPaidComponent implements OnInit {
  readonly shouldDisplayAmount$ = this.store.pipe(map((state) => !shouldHidePaymentAmount(state)));
  readonly shouldDisplayConfirmationInfo$ = this.store.pipe(
    map((state) => trackShouldDisplayMarkPaidConfirmationInfo(state.requestTaskItem?.requestInfo?.type)),
  );
  today = new Date();

  confirmed$ = new BehaviorSubject<boolean>(false);
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  form: UntypedFormGroup;

  details$ = this.store.pipe(
    first(),
    map((state) => mapTrackPaymentToPaymentDetails(state)),
  );

  constructor(
    readonly store: PaymentStore,
    private readonly fb: UntypedFormBuilder,
    private readonly pendingRequest: PendingRequestService,
    private readonly destroy$: DestroySubject,
    private readonly backLinkService: BackLinkService,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.store.subscribe((state) => {
      this.breadcrumbService.show([
        {
          text: getHeadingMap((state.requestTaskItem?.requestInfo?.requestMetadata as any)?.year)[
            state.requestTaskItem?.requestTask.type
          ],
          link: [getPaymentBaseLink(state.requestType) + 'payment', state.requestTaskId, 'track'],
        },
      ]);
    });

    this.confirmed$.pipe(takeUntil(this.destroy$)).subscribe((res) => {
      if (res) {
        this.backLinkService.hide();
      } else {
        this.backLinkService.show();
      }
    });

    this.store.pipe(first()).subscribe(
      (state) =>
        (this.form = this.fb.group({
          receivedDate: [{ value: null, disabled: !state.isEditable }],
        })),
    );
  }

  submitForm(): void {
    if (this.form.valid) {
      this.store
        .postTrackPaymentAsPaid({ ...this.form.value })
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.confirmed$.next(true));
    } else {
      this.isSummaryDisplayed$.next(true);
      this.form.markAllAsTouched();
    }
  }
}
