import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AviationAccoundDetailsHistoryCategoryPipe } from '@aviation/accounts/pipes/account-details-history-category.pipe';
import { PipesModule } from '@shared/pipes/pipes.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { AccountDetailsHistoryDTO } from 'pmrv-api';

@Component({
  selector: 'app-aviation-account-details-history-list',
  imports: [GovukComponentsModule, PipesModule, AviationAccoundDetailsHistoryCategoryPipe],
  templateUrl: './aviation-account-details-history-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AviationAccountDetailsListComponent {
  @Input() history: AccountDetailsHistoryDTO[];
  @Input() columns: GovukTableColumn[];
}
