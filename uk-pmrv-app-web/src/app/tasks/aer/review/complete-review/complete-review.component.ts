import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-aer-complete-review',
  template: `
    <ng-container *ngIf="(isSubmitted$ | async) === false; else completed">
      <app-aer-task-review>
        <app-page-heading size="xl">Are you sure you want to complete this task?</app-page-heading>
        <div class="govuk-button-group">
          <button type="button" appPendingButton govukButton (click)="complete()">Yes, complete this task</button>
        </div>
      </app-aer-task-review>
    </ng-container>
    <ng-template #completed>
      <div class="govuk-grid-row">
        <govuk-panel title="Task completed"></govuk-panel>
        <a govukLink routerLink="/dashboard"> Return to dashboard </a>
      </div>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompleteReviewComponent implements OnInit {
  isSubmitted$ = new BehaviorSubject<boolean>(false);

  constructor(
    readonly aerService: AerService,
    private store: CommonTasksStore,
    private readonly pendingRequest: PendingRequestService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  complete(): void {
    this.aerService
      .postSubmit('AER_COMPLETE_REVIEW')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.isSubmitted$.next(true);
        this.backLinkService.hide();
      });
  }
}
