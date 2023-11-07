import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload, RegulatedActivity } from 'pmrv-api';

@Component({
  selector: 'app-regulated-activity-delete',
  template: `
    <ng-container *ngIf="activityType$ | async as activityType">
      <app-page-heading size="xl">
        Are you sure you want to delete
        <span class="nowrap"> ‘{{ activityType | regulatedActivityType }}’? </span>
      </app-page-heading>

      <p class="govuk-body">Any reference to this item will be removed from your application.</p>

      <div class="govuk-button-group">
        <button type="button" (click)="onDelete()" appPendingButton govukWarnButton>Yes, delete</button>
        <a govukLink routerLink="." (click)="this.location.back()">Cancel</a>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatedActivityDeleteComponent implements PendingRequest {
  activityType$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('activityType'))) as Observable<
    RegulatedActivity['type']
  >;

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly location: Location,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly aerService: AerService,
  ) {}

  onDelete(): void {
    this.aerService
      .getPayload()
      .pipe(
        first(),
        map(
          (payload) =>
            (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.opinionStatement,
        ),
        withLatestFrom(this.activityType$),
        switchMap(([opinionStatementData, activityType]) => {
          opinionStatementData.regulatedActivities = opinionStatementData.regulatedActivities.filter(
            (regulatedActivity) => regulatedActivity !== activityType,
          );
          return this.aerService.postVerificationTaskSave(
            {
              opinionStatement: {
                ...opinionStatementData,
              },
            },
            false,
            'opinionStatement',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
