import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AttachedFile } from '@shared/types/attached-file.type';

import { VerificationReportOfTheActivityLevelReport } from 'pmrv-api';

@Component({
  selector: 'app-verification-report-summary-template',
  templateUrl: './verification-report-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationReportSummaryTemplateComponent {
  @Input() verificationActivityLevelReport: VerificationReportOfTheActivityLevelReport;
  @Input() editable: boolean;
  @Input() document: AttachedFile;

  get documents() {
    return [this.document];
  }
}
