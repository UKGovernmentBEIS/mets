import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DoalService } from '@tasks/doal/core/doal.service';

import { DoalGrantAuthorityResponse } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  template: `
    <app-doal-task [breadcrumb]="true">
      <app-page-heading>Check your answers</app-page-heading>

      <app-doal-authority-decision-template
        [data]="authorityResponse$ | async"
        [documents]="documentFiles$ | async"
        [editable]="isEditable$ | async"
      ></app-doal-authority-decision-template>

      <div class="govuk-button-group" *ngIf="isEditable$ | async">
        <button (click)="onSubmit()" appPendingButton govukButton type="button">Confirm and complete</button>
      </div>

      <app-task-return-link [levelsUp]="2" taskType="DOAL_AUTHORITY_RESPONSE"></app-task-return-link>
    </app-doal-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  isEditable$ = this.doalService.isEditable$;
  authorityResponse$ = this.doalService.authorityPayload$.pipe(
    map((payload) => payload.doalAuthority.authorityResponse),
  );
  documentFiles$ = this.authorityResponse$.pipe(
    map((authorityResponse) => (authorityResponse as DoalGrantAuthorityResponse)?.documents ?? []),
    map((files) => this.doalService.getDownloadUrlFiles(files)),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.doalService
      .saveDoalAuthority(undefined, 'authorityResponse', true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
