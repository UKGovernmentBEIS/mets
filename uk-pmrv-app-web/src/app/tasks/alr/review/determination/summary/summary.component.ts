import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AlrDeterminationSummaryTemplateComponent } from '@shared/components/alr/determination-summary-template/determination-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import {
  ALRApplicationRegulatorReviewOutcome,
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRClosedDetermination,
  DoalProceedToAuthorityDetermination,
} from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  alrFile: AttachedFile;
  files: AttachedFile[];
  isSubmitDisplayed: boolean;
}

@Component({
  selector: 'app-alr-determination-summary',
  imports: [SharedModule, AlrTaskSharedModule, AlrDeterminationSummaryTemplateComponent],
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrDeterminationSummaryComponent {
  isEditable = this.alrService.isEditable;
  payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  determination = computed(() => {
    const payload = this.payload();
    return payload.regulatorReviewOutcome?.determination as
      | DoalProceedToAuthorityDetermination
      | ALRClosedDetermination;
  });

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.payload();
    const determinationSectionsCompleted = payload.regulatorReviewSectionsCompleted['DETERMINATION'];
    const determination = payload.regulatorReviewOutcome.determination as ALRClosedDetermination;
    const alrFile = this.alrService.getRegulatorDownloadUrlAlrFile(determination.alrFile || payload.alr?.alrFile);
    const files = this.alrService.getRegulatorDownloadUrlFiles(determination.files || payload.alr?.files);

    return { isEditable, alrFile, files, isSubmitDisplayed: !determinationSectionsCompleted && isEditable };
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const payload = this.payload();
    const { alr, regulatorReviewOutcome } = payload;
    const determination = regulatorReviewOutcome?.determination as ALRClosedDetermination;

    this.alrService
      .postAlrReview(
        {
          ...regulatorReviewOutcome,
          determination: {
            ...regulatorReviewOutcome?.determination,
            ...(determination?.type === 'CLOSED_ALR'
              ? { alrFile: determination?.alrFile || alr.alrFile, files: determination?.files || alr.files }
              : {}),
          },
        } as ALRApplicationRegulatorReviewOutcome,
        'DETERMINATION',
        true,
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route }));
  }
}
