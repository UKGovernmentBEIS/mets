import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { TransportCO2AndN2OPipelineSystems } from 'pmrv-api';

@Component({
  selector: 'app-transferred-co2-pipeline-summary-template',
  templateUrl: './transferred-co2-pipeline-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferredCo2PipelineSummaryTemplateComponent {
  @Input() pipelineSystems: TransportCO2AndN2OPipelineSystems;
  @Input() cssClass: string;
}
