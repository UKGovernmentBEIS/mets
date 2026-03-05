import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { VirTaskStatusPipe } from '@aviation/shared/pipes/vir-task-status.pipe';
import { SharedModule } from '@shared/shared.module';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import {
  AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  AviationVirApplicationReviewRequestTaskPayload,
  AviationVirApplicationSubmitRequestTaskPayload,
  RequestTaskPayload,
  VirVerificationData,
} from 'pmrv-api';

@Component({
  selector: 'app-vir-task-list',
  templateUrl: './vir-task-list.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [VirSharedModule, SharedModule, VirTaskStatusPipe],
})
export class VirTaskListComponent {
  virRequestPayload: RequestTaskPayload &
    AviationVirApplicationSubmitRequestTaskPayload &
    AviationVirApplicationReviewRequestTaskPayload &
    AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload;
  verificationDataGroup: VerificationDataItem[] = [];
  operatorImprovementResponses: AviationVirApplicationReviewRequestTaskPayload['operatorImprovementResponses'] = {};
  regulatorImprovementResponses: AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload['regulatorImprovementResponses'] =
    {};

  @Input() set virPayload(
    value: RequestTaskPayload &
      AviationVirApplicationSubmitRequestTaskPayload &
      AviationVirApplicationReviewRequestTaskPayload &
      AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  ) {
    this.virRequestPayload = value;

    switch (value?.payloadType) {
      case 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD':
        this.setSubmitData(value?.verificationData);
        break;
      case 'AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD':
        this.operatorImprovementResponses = value?.operatorImprovementResponses ?? {};
        break;
      case 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD':
        this.regulatorImprovementResponses = value?.regulatorImprovementResponses ?? {};
        break;
    }
  }

  private setSubmitData(verificationData: VirVerificationData): void {
    this.verificationDataGroup = [
      ...(verificationData?.uncorrectedNonConformities
        ? Object.values(verificationData?.uncorrectedNonConformities)
        : []),
      ...(verificationData?.recommendedImprovements ? Object.values(verificationData?.recommendedImprovements) : []),
      ...(verificationData?.priorYearIssues ? Object.values(verificationData?.priorYearIssues) : []),
    ];
  }
}
