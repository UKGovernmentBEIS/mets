import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-mark-paid',
  template: `
    <app-page-heading>Are you sure you want to mark this payment as paid?</app-page-heading>
    <div class="govuk-button-group" *ngIf="(store.isEditable$ | async) === true">
      <button (click)="onComplete()" appPendingButton govukButton type="button">Confirm and complete</button>
    </div>

    <app-return-link
      [requestTaskType]="(store | async).requestTaskItem.requestTask.type"
      [requestMetadata]="(store | async).requestTaskItem.requestInfo.requestMetadata"
      returnLink=".."></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MarkPaidComponent {
  taskId$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.requestTask.id),
  );

  constructor(
    readonly store: PaymentStore,
    private readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  onComplete(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.store.postMarkAsPaid({
            ...state,
            completed: true,
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../confirmation'], { relativeTo: this.route, queryParams: { method: 'BANK_TRANSFER' } });
        this.breadcrumbService.showDashboardBreadcrumb(this.router.url);
      });
  }
}
