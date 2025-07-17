import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-identified-changes-delete',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to delete
      <span class="nowrap">'{{ item$ | async }}'?</span>
    </app-page-heading>
    <p class="govuk-body">Any reference to this item will be removed from your application.</p>
    <div class="govuk-button-group">
      <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      <a routerLink="../.." govukLink>Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IdentifiedChangesDeleteComponent {
  item$ = combineLatest([this.route.paramMap, this.aerService.getPayload()]).pipe(
    first(),
    map(
      ([paramMap, payload]) =>
        (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.summaryOfConditions
          .notReportedChanges[Number(paramMap.get('index'))],
    ),
    map((item) => `${item.reference} ${item.explanation}`),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  delete(): void {
    combineLatest([this.route.paramMap, this.aerService.getPayload()])
      .pipe(
        first(),
        switchMap(([paramMap, payload]) =>
          this.aerService.postVerificationTaskSave(
            {
              summaryOfConditions: {
                ...(payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
                  .summaryOfConditions,
                notReportedChanges: (
                  payload as AerApplicationVerificationSubmitRequestTaskPayload
                ).verificationReport.summaryOfConditions.notReportedChanges
                  .filter((item, idx) => idx !== Number(paramMap.get('index')))
                  .map((item, idx) => ({
                    reference: `B${idx + 1}`,
                    explanation: item.explanation,
                  })),
              },
            },
            false,
            'summaryOfConditions',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
