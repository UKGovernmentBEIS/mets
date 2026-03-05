import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { VirTaskStatusPipe } from '@aviation/shared/pipes/vir-task-status.pipe';
import { SharedModule } from '@shared/shared.module';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import {
  AviationVirApplicationReviewedRequestActionPayload,
  AviationVirApplicationSubmittedRequestActionPayload,
  OperatorImprovementResponse,
  RequestActionPayload,
  VirVerificationData,
} from 'pmrv-api';

@Component({
  selector: 'app-vir-action-task-list',
  templateUrl: './vir-action-task-list.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [VirSharedModule, SharedModule, VirTaskStatusPipe],
})
export class VirActionTaskListComponent {
  virRequestActionPayload: RequestActionPayload &
    AviationVirApplicationSubmittedRequestActionPayload &
    AviationVirApplicationReviewedRequestActionPayload;

  verificationDataGroup: VerificationDataItem[] = [];
  operatorImprovementResponse: { [p: string]: OperatorImprovementResponse };

  @Input() set virPayload(value: RequestActionPayload) {
    this.virRequestActionPayload = value as RequestActionPayload &
      AviationVirApplicationSubmittedRequestActionPayload &
      AviationVirApplicationReviewedRequestActionPayload;

    switch (value?.payloadType) {
      case 'AVIATION_VIR_APPLICATION_SUBMITTED_PAYLOAD':
        this.setSubmitData(this.virRequestActionPayload?.verificationData);
        break;
      case 'AVIATION_VIR_APPLICATION_REVIEWED_PAYLOAD':
        this.setReviewData();
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

  private setReviewData(): void {
    this.operatorImprovementResponse = this.virRequestActionPayload.operatorImprovementResponses;
  }
}
