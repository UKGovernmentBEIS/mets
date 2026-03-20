import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

@Component({
  selector: 'app-bdrs2-complete-review',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  template: `
    <app-bdrs2-task-review>
      <app-page-heading size="xl">
        Are you sure you want to complete the stage 2 baseline data report task?
      </app-page-heading>
      <div class="govuk-button-group">
        <button type="button" appPendingButton govukButton (click)="complete()">Yes, complete the task</button>
      </div>
    </app-bdrs2-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2CompleteReviewComponent {
  constructor(
    readonly bdrs2Service: BdrS2Service,
    private readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    private route: ActivatedRoute,
  ) {}

  complete(): void {
    this.bdrs2Service
      .postSubmit('BDRS2_REGULATOR_REVIEW_SUBMIT')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.router.navigate(['confirmation'], { relativeTo: this.route });
      });
  }
}
