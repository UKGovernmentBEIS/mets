import { DatePipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { GovukComponentsModule } from 'govuk-components';

import { EmpApplicationTimeframeInfo } from 'pmrv-api';

@Component({
  selector: 'app-application-timeframe-summary-template',
  imports: [DatePipe, GovukComponentsModule, NgIf, RouterLink],
  templateUrl: './application-timeframe-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationTimeframeSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: EmpApplicationTimeframeInfo;
  @Input() changeUrlQueryParams: Params = {};
}
