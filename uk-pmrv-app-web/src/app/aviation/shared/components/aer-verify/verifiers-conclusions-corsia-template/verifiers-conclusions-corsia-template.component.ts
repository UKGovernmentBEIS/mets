import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { MaterialityThresholdTypePipe } from '@aviation/shared/pipes/materiality-threshold-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaVerifiersConclusions } from 'pmrv-api';

@Component({
  selector: 'app-verifiers-conclusions-corsia-template',
  templateUrl: './verifiers-conclusions-corsia-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink, MaterialityThresholdTypePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifiersConclusionsCorsiaTemplateComponent {
  @Input() data: AviationAerCorsiaVerifiersConclusions;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
