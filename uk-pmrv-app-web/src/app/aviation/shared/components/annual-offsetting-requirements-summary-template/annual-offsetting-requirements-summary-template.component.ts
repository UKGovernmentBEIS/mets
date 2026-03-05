import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaAnnualOffsetting } from 'pmrv-api';

@Component({
  selector: 'app-annual-offsetting-requirements-summary-template',
  standalone: true,
  imports: [RouterLink, SharedModule],
  templateUrl: './annual-offsetting-requirements-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnnualOffsettingRequirementsSummaryTemplateComponent {
  @Input() annualOffsetting: AviationAerCorsiaAnnualOffsetting;
  @Input() isEditable: boolean;
}
