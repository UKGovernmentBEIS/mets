import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { determinationReasonTypesLabelsMap } from '@tasks/dre/submit/determination-reason/determination-reason-type-label.map';

import { Dre } from 'pmrv-api';

@Component({
  selector: 'app-determination-reason-summary-template',
  templateUrl: './determination-reason-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationReasonSummaryTemplateComponent {
  @Input() cssClass: string;
  @Input() dre: Dre;
  @Input() editable: boolean;
  @Input() supportingDocumentFiles: {
    downloadUrl: string;
    fileName: string;
  }[];

  determinationReasonTypesLabelsMap = determinationReasonTypesLabelsMap;
}
