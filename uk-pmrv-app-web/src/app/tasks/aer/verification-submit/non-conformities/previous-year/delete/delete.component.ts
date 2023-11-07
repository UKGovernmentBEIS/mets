import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-delete',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to delete
      <span class="nowrap"> '{{ item$ | async }}'? </span>
    </app-page-heading>
    <p class="govuk-body">Any reference to this item will be removed from your application.</p>
    <div class="govuk-button-group">
      <button type="button" (click)="onDelete()" appPendingButton govukWarnButton>Yes, delete</button>
      <a govukLink routerLink="." (click)="this.location.back()">Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  item$ = combineLatest([this.route.paramMap, this.aerService.getPayload()]).pipe(
    first(),
    map(
      ([paramMap, payload]) =>
        (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.uncorrectedNonConformities
          .priorYearIssues[Number(paramMap.get('index'))],
    ),
    map((item) => `${item.reference} ${item.explanation}`),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly location: Location,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onDelete(): void {
    combineLatest([this.route.paramMap, this.aerService.getPayload()])
      .pipe(
        first(),
        switchMap(([paramMap, payload]) =>
          this.aerService.postVerificationTaskSave(
            {
              uncorrectedNonConformities: {
                ...(payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
                  .uncorrectedNonConformities,
                priorYearIssues: (
                  payload as AerApplicationVerificationSubmitRequestTaskPayload
                ).verificationReport.uncorrectedNonConformities.priorYearIssues
                  .filter((item, idx) => idx !== Number(paramMap.get('index')))
                  .map((item, idx) => ({
                    reference: `E${idx + 1}`,
                    explanation: item.explanation,
                  })),
              },
            },
            false,
            'uncorrectedNonConformities',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../../list'], { relativeTo: this.route }));
  }
}
