import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { EmissionsReductionClaimListTemplateComponent } from '@aviation/shared/components/aer/emissions-reduction-claim-list-template/emissions-reduction-claim-list-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerSaf, AviationAerSafPurchase } from 'pmrv-api';

@Component({
  selector: 'app-aer-emissions-reduction-claim-summary-template',
  templateUrl: './emissions-reduction-claim-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, EmissionsReductionClaimListTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimSummaryTemplateComponent {
  @Input() data: AviationAerSaf;
  @Input() declarationFile: { downloadUrl: string; fileName: string };
  @Input() purchases: {
    purchase: AviationAerSafPurchase;
    files: { downloadUrl: string; fileName: string }[];
  }[];
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
