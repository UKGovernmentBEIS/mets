import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types';
import { NerService } from '@tasks/ner/core';

import { NERApplicationRegulatorReviewOutcome } from 'pmrv-api';

@Component({
  selector: 'app-ner-review-outcome-summary',
  imports: [SharedModule, RouterLink],
  templateUrl: './review-outcome-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerReviewOutcomeSummaryComponent {
  @Input() outcome: NERApplicationRegulatorReviewOutcome;
  @Input() isEditable: boolean;
  @Input() nerFile: AttachedFile;
  @Input() supportingFiles: Array<AttachedFile>;
  @Input() hideSubmit: boolean;

  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly payload = this.nerService.payload;

  onConfirm() {
    const payload = this.payload();

    this.nerService
      .postRegulatorTaskSave(
        {
          ...payload?.regulatorReviewOutcome,
        },
        true,
        'OUTCOME',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
