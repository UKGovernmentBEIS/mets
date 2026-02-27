import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Bdrs2ReviewOutcomeOpinionPipe } from '@shared/pipes/bdrs2/bdrs2-review-outcome-opinion.pipe';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export interface ViewModel {
  bdrs2: BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload['bdrs2'];
  outcome: BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload['regulatorReviewOutcome'];
  bdrFile: AttachedFile;
  files: AttachedFile[];
  isEditable: boolean;
}

@Component({
  selector: 'app-outcome-summary-template',
  imports: [SharedModule, RouterLink, Bdrs2ReviewOutcomeOpinionPipe],
  templateUrl: './outcome-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeSummaryTemplateComponent {
  @Input() vm: ViewModel;

  notNullOrUndefined(value: unknown): boolean {
    return value !== null && value !== undefined && value !== '';
  }

  withdrawn(vm: ViewModel): boolean {
    return vm.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'WITHDRAW';
  }
}
