import { Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { AerDataReviewDecision } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-aer-review-decision-group-summary',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './aer-review-decision-group-summary.component.html',
})
export class AerReviewDecisionGroupSummaryComponent {
  @Input() data: AerDataReviewDecision;
  @Input() attachments: { [key: string]: string };
  @Input() downloadBaseUrl: string;

  readonly aerReviewGroupDecision: Record<AerDataReviewDecision['type'], string> = {
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
