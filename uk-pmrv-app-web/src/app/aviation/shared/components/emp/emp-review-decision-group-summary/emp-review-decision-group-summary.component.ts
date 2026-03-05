import { Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { EmpIssuanceReviewDecision } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-emp-review-decision-group-summary',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './emp-review-decision-group-summary.component.html',
})
export class EmpReviewDecisionGroupSummaryComponent {
  @Input() data: EmpIssuanceReviewDecision;
  @Input() attachments: { [key: string]: string };
  @Input() downloadBaseUrl: string;

  readonly empReviewGroupDecision: Record<EmpIssuanceReviewDecision['type'], string> = {
    ACCEPTED: 'Accepted',
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
