import { Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { EmpVariationReviewDecision } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-emp-variation-review-decision-group-summary',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './emp-variation-review-decision-group-summary.component.html',
})
export class EmpVariationReviewDecisionGroupSummaryComponent {
  @Input() data: EmpVariationReviewDecision;
  @Input() attachments: { [key: string]: string };
  @Input() downloadBaseUrl: string;

  readonly empVariationReviewGroupDecision: Record<EmpVariationReviewDecision['type'], string> = {
    ACCEPTED: 'Accepted',
    REJECTED: 'Rejected',
    OPERATOR_AMENDS_NEEDED: 'Operator changes required',
  };

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    return (
      files?.map((file) => ({
        fileName: this.attachments[file],
        downloadUrl: this.downloadBaseUrl + `/${file}`,
      })) ?? []
    );
  }
}
