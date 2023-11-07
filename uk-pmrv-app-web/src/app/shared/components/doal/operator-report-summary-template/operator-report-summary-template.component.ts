import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AttachedFile } from '@shared/types/attached-file.type';

import { OperatorActivityLevelReport } from 'pmrv-api';

@Component({
  selector: 'app-operator-report-summary-template',
  templateUrl: './operator-report-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorReportSummaryTemplateComponent {
  @Input() operatorActivityLevelReport: OperatorActivityLevelReport;
  @Input() editable: boolean;
  @Input() document: AttachedFile;

  get documents() {
    return [this.document];
  }
}
