import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationAuthorityReviewOutcome, ALRAuthorityResponseSubmitRequestTaskPayload } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  submissionDate: ALRApplicationAuthorityReviewOutcome['submissionDate'];
  isSubmitDisplayed: boolean;
}

@Component({
  selector: 'app-alr-authority-summary',
  standalone: true,
  imports: [AlrTaskSharedModule, SharedModule, RouterLink],
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAuthoritySummaryComponent {
  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.payload();
    const SectionCompleted = payload.authorityReviewSectionsCompleted['applicationSubmitted'];
    const submissionDate = payload.authorityReviewOutcome.submissionDate;

    return { isEditable, submissionDate, isSubmitDisplayed: !SectionCompleted && isEditable };
  });

  private readonly isEditable = this.alrService.isEditable;
  private readonly payload = this.alrService.payload as Signal<ALRAuthorityResponseSubmitRequestTaskPayload>;

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    this.alrService
      .postAlrAuthority({}, 'applicationSubmitted', true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
