import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-opinion-statement-total-emissions-summary-template',
  templateUrl: './opinion-statement-total-emissions-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementTotalEmissionsSummaryTemplateComponent {
  @Input() totalEmissionsProvided: string;
  @Input() emissionsCorrect: boolean;
  @Input() manuallyProvidedEmissions: string;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
