import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload, VerifiedWithCommentsOverallAssessment } from 'pmrv-api';

@Component({
  selector: 'app-reason-item-delete',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to delete
      <span class="nowrap"> '{{ item$ | async }}'? </span>
    </app-page-heading>
    <p class="govuk-body">Any reference to this item will be removed from your application.</p>
    <div class="govuk-button-group">
      <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      <a routerLink="../.." govukLink>Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReasonItemDeleteComponent {
  item$ = combineLatest([this.route.paramMap, this.aerService.getPayload()]).pipe(
    first(),
    map(([paramMap, payload]) => {
      const overallAssessmentInfo = (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
        .overallAssessment;

      return (overallAssessmentInfo as VerifiedWithCommentsOverallAssessment).reasons[Number(paramMap.get('index'))];
    }),
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
        switchMap(([paramMap, payload]) => {
          const overallAssessmentInfo = (payload as AerApplicationVerificationSubmitRequestTaskPayload)
            .verificationReport.overallAssessment;

          return this.aerService.postVerificationTaskSave(
            {
              overallAssessment: {
                ...overallAssessmentInfo,
                reasons: (overallAssessmentInfo as VerifiedWithCommentsOverallAssessment).reasons.filter(
                  (item, idx) => idx !== Number(paramMap.get('index')),
                ),
              },
            },
            false,
            'overallAssessment',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
