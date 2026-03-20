import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaEmissionsReductionClaimVerification } from 'pmrv-api';

@Component({
  selector: 'app-emissions-reduction-claim-corsia-template',
  imports: [SharedModule, RouterLink],
  templateUrl: './emissions-reduction-claim-corsia-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimCorsiaTemplateComponent {
  @Input() data: AviationAerCorsiaEmissionsReductionClaimVerification;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
