import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AviationAccountDetails } from '../../store';

@Component({
  selector: 'app-aviation-account-summary-info',
  templateUrl: './aviation-account-summary-info.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AviationAccountSummaryInfoComponent {
  @Input() summaryInfo: AviationAccountDetails;
  @Input() formRouterLink = 'edit';
  @Input() withRegistryId: boolean;
  @Input() editable = true;
  @Input() isAddressEditable = true;
}
