import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaProcessAnalysis } from 'pmrv-api';

@Component({
  selector: 'app-process-analysis-corsia-template',
  templateUrl: './process-analysis-corsia-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessAnalysisCorsiaTemplateComponent {
  @Input() data: AviationAerCorsiaProcessAnalysis;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
