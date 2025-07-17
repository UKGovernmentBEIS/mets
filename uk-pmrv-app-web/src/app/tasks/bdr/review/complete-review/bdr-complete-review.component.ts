import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { BdrService, BdrTaskSharedModule } from '@tasks/bdr/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

@Component({
  selector: 'app-bdr-complete-review',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  template: `
    <app-bdr-task-review>
      <app-page-heading size="xl">Are you sure you want to complete the baseline data report task?</app-page-heading>
      <div class="govuk-button-group">
        <button type="button" appPendingButton govukButton (click)="complete()">Yes, complete the task</button>
      </div>
    </app-bdr-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrCompleteReviewComponent {
  constructor(
    readonly bdrService: BdrService,
    private readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    private route: ActivatedRoute,
  ) {}

  complete(): void {
    this.bdrService
      .postSubmit('BDR_REGULATOR_REVIEW_SUBMIT')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.router.navigate(['confirmation'], { relativeTo: this.route });
      });
  }
}
