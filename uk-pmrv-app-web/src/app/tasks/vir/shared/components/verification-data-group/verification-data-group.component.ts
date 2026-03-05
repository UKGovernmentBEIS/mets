import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { VirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-verification-data-group',
  templateUrl: './verification-data-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationDataGroupComponent {
  verificationDataGroup: VerificationDataItem[] = [];
  virRequestPayload: VirApplicationSubmitRequestTaskPayload;

  @Input() set virPayload(value: VirApplicationSubmitRequestTaskPayload) {
    this.virRequestPayload = value;
    const verificationData = value?.verificationData;
    this.verificationDataGroup = [
      ...(verificationData?.uncorrectedNonConformities
        ? Object.values(verificationData?.uncorrectedNonConformities)
        : []),
      ...(verificationData?.recommendedImprovements ? Object.values(verificationData?.recommendedImprovements) : []),
      ...(verificationData?.priorYearIssues ? Object.values(verificationData?.priorYearIssues) : []),
    ];
  }
}
