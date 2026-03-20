import { ChangeDetectionStrategy, Component, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-alr-activity-level-delete',
  imports: [SharedModule, RouterLink],
  template: `
    <app-page-heading size="xl">Are you sure you want to delete this item?</app-page-heading>

    <p class="govuk-body">Any reference to this item will be removed from your application.</p>

    <div class="govuk-button-group" *ngIf="isEditable()">
      <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      <a routerLink="../.." govukLink>Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrActivityLevelDeleteComponent {
  private readonly index = this.route.snapshot.paramMap.get('index');

  payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  isEditable = this.alrService.isEditable;

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  delete() {
    const payload = this.payload();

    this.alrService
      .postAlrReview(
        {
          ...payload.regulatorReviewOutcome,
          activityLevels: payload.regulatorReviewOutcome?.activityLevels?.filter((_, i) => i !== Number(this.index)),
        },
        'ALC',
        false,
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
