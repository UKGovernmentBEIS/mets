import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AviationAccountDetails } from '../../store';

@Component({
  selector: 'app-aviation-account-summary-info',
  standalone: false,
  templateUrl: './aviation-account-summary-info.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AviationAccountSummaryInfoComponent {
  @Input() summaryInfo: AviationAccountDetails;
  @Input() formRouterLink = 'edit';
  @Input() withRegistryId: boolean;
  @Input() editable = true;
  @Input() isAddressEditable = true;
  @Input() editModeEnabled = false;
}
