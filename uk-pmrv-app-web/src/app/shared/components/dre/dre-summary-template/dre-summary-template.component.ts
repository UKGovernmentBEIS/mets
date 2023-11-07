import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { Dre } from 'pmrv-api';

@Component({
  selector: 'app-dre-summary-template',
  templateUrl: './dre-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DreSummaryComponent {
  @Input() cssClass: string;
  @Input() dre: Dre;
  @Input() isEditable: boolean;
  @Input() supportingDocumentFiles: {
    downloadUrl: string;
    fileName: string;
  }[];
}
