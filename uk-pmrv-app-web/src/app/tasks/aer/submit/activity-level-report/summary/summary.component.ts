import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  template: `
    <app-aer-task [breadcrumb]="true">
      <app-page-heading>Check your answers</app-page-heading>
      <app-activity-level-report-group
        [activityLevelReport]="activityLevelReport$ | async"
        [documentFiles]="documentFiles$ | async"
        [isEditable]="isEditable$ | async"></app-activity-level-report-group>
      <div class="govuk-button-group" *ngIf="isEditable$ | async">
        <button (click)="onSubmit()" appPendingButton govukButton type="button">Confirm and complete</button>
      </div>
      <app-return-link></app-return-link>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  isEditable$ = this.aerService.isEditable$;
  activityLevelReport$ = (this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload.aer?.activityLevelReport),
  );
  documentFiles$ = this.activityLevelReport$.pipe(
    map((activityLevelReport) => activityLevelReport?.file),
    map((file) => (file ? this.aerService.getDownloadUrlFiles([file]) : [])),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.aerService
      .postTaskSave(undefined, undefined, true, 'activityLevelReport')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
