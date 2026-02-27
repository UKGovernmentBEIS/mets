import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

@Component({
  selector: 'app-aer-emissions-reduction-claim-corsia-template',
  imports: [SharedModule, RouterLink],
  templateUrl: './aer-emissions-reduction-claim-corsia-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerEmissionsReductionClaimCorsiaTemplateComponent {
  @Input() emissionsReductionClaim: AviationAerCorsiaEmissionsReductionClaim;
  @Input() cefFiles: AttachedFile[];
  @Input() declarationFiles: AttachedFile[];
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
