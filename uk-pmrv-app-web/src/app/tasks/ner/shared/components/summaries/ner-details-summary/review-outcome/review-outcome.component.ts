import { ChangeDetectionStrategy, Component, computed, inject, Input, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types';
import { NER_TASK_FORM, NerService } from '@tasks/ner/core';
import { NerReviewOutcomeSummaryComponent } from '@tasks/ner/review';

import { NERApplicationRegulatorReviewOutcome } from 'pmrv-api';

import { nerReviewOutcomeFormProvider } from './review-outcome-form.provider';

interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  outcome: NERApplicationRegulatorReviewOutcome;
  nerFile: AttachedFile;
  supportingFiles: Array<AttachedFile>;
}

@Component({
  selector: 'app-ner-review-outcome',
  imports: [SharedModule, NerReviewOutcomeSummaryComponent],
  templateUrl: './review-outcome.component.html',
  providers: [nerReviewOutcomeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerReviewOutcomeComponent {
  @Input() enableSummary: boolean;

  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly payload = this.nerService.payload;
  private readonly isEditable = this.nerService.isEditable;

  form = inject<UntypedFormGroup>(NER_TASK_FORM);
  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();

    return {
      isEditable: this.isEditable(),
      hideSubmit: payload.regulatorReviewSectionsCompleted?.['OUTCOME'],
      outcome: payload.regulatorReviewOutcome,
      nerFile: this.nerService.getRegulatorDownloadUrlFile(payload.regulatorReviewOutcome?.nerFile),
      supportingFiles: this.nerService.getRegulatorDownloadUrlFiles(payload.regulatorReviewOutcome?.supportingFiles),
    };
  });

  onSubmit() {
    const changing = this.route.snapshot.data?.changing;
    const nextRoute = changing ? ['.', 'summary'] : ['.', 'upload-ner'];

    if (this.form.dirty) {
      this.nerService
        .postRegulatorTaskSave(
          {
            opinion: this.form.value.opinion,
            notes: this.form.value.notes,
          },
          false,
          'OUTCOME',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(nextRoute, { relativeTo: this.route }));
    } else {
      this.router.navigate(nextRoute, { relativeTo: this.route });
    }
  }
}
