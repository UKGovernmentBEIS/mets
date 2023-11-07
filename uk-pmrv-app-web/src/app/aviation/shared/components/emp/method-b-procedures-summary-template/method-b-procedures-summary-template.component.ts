import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink, RouterLinkWithHref } from '@angular/router';

import { GovukComponentsModule } from 'govuk-components';

import { EmpMethodBProcedures } from 'pmrv-api';

import { ProcedureFormSummaryComponent } from '../../procedure-form-summary';

@Component({
  selector: 'app-method-b-procedures-summary-template',
  standalone: true,
  imports: [GovukComponentsModule, RouterLinkWithHref, ProcedureFormSummaryComponent, RouterLink, NgIf],
  templateUrl: './method-b-procedures-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MethodBProceduresSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: EmpMethodBProcedures;
}
