import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AlrAuthoritySummaryTemplateComponent } from '@shared/components/alr/authority-summary-template/authority-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRGrantAuthorityResponse } from 'pmrv-api';

@Component({
  selector: 'app-alr-authority-summary',
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule, AlrAuthoritySummaryTemplateComponent],
  template: `
    <app-alr-task-common
      returnLink="../../"
      returnLinkTitle="Provide UK ETS Authority response for activity Level Change"
      [breadcrumb]="true">
      <app-page-heading caption="Provide UK ETS Authority reponse">Check your answers</app-page-heading>

      <app-alr-authority-summary-template
        [data]="authorityResponse$ | async"
        [documents]="documentFiles$ | async"
        [editable]="isEditable$ | async"></app-alr-authority-summary-template>

      <div class="govuk-button-group" *ngIf="isEditable$ | async">
        <button (click)="onSubmit()" appPendingButton govukButton type="button">Confirm and complete</button>
      </div>
    </app-alr-task-common>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrResponseSummaryComponent {
  isEditable$ = this.alrService.isEditable$;
  authorityResponse$ = this.alrService.authorityPayload$.pipe(
    map((payload) => payload.authorityReviewOutcome.authorityResponse),
  );
  documentFiles$ = this.authorityResponse$.pipe(
    map((authorityResponse) => (authorityResponse as ALRGrantAuthorityResponse)?.documents ?? []),
    map((files) => this.alrService.getOperatorDownloadUrlFiles(files)),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.alrService
      .postAlrAuthority(undefined, 'authorityResponse', true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
