import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { RequestInfoDTO } from 'pmrv-api';

@Component({
  selector: 'app-choose-workflow-summary-template',
  templateUrl: './choose-workflow-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChooseWorkflowSummaryTemplateComponent {
  @Input() data: Array<RequestInfoDTO>;
  @Input() editable: boolean;
  @Input() baseChangeLink: string;
}
