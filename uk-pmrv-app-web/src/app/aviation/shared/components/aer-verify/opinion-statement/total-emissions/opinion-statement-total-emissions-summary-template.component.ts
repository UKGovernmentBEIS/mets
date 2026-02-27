import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-opinion-statement-total-emissions-summary-template',
  imports: [SharedModule, RouterLinkWithHref],
  templateUrl: './opinion-statement-total-emissions-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementTotalEmissionsSummaryTemplateComponent {
  @Input() totalEmissionsProvided: string;
  @Input() emissionsCorrect: boolean;
  @Input() manuallyProvidedEmissions: string;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
