import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { GovukComponentsModule } from 'govuk-components';

import { EmpEmissionsReductionClaim } from 'pmrv-api';

import { ProcedureFormSummaryComponent } from '../../procedure-form-summary';

@Component({
  selector: 'app-emissions-reduction-claim-summary-template',
  standalone: true,
  imports: [GovukComponentsModule, RouterLinkWithHref, ProcedureFormSummaryComponent, NgIf],
  templateUrl: './emissions-reduction-claim-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: EmpEmissionsReductionClaim;
}
