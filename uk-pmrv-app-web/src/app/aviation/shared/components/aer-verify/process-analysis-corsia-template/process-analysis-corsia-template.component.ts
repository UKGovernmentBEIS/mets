import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaProcessAnalysis } from 'pmrv-api';

@Component({
  selector: 'app-process-analysis-corsia-template',
  imports: [SharedModule, RouterLink],
  templateUrl: './process-analysis-corsia-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessAnalysisCorsiaTemplateComponent {
  @Input() data: AviationAerCorsiaProcessAnalysis;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
