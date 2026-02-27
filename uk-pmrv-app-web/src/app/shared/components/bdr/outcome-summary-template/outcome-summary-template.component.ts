import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export interface ViewModel {
  bdr: BDRApplicationRegulatorReviewSubmitRequestTaskPayload['bdr'];
  outcome: BDRApplicationRegulatorReviewSubmitRequestTaskPayload['regulatorReviewOutcome'];
  bdrFile: AttachedFile;
  files: AttachedFile[];
  isEditable: boolean;
}

@Component({
  selector: 'app-outcome-summary-template',
  imports: [SharedModule, RouterLink],
  templateUrl: './outcome-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeSummaryTemplateComponent {
  @Input() vm: ViewModel;
}
