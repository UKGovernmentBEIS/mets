import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukComponentsModule } from 'govuk-components';

import { EmpMethodBProcedures } from 'pmrv-api';

import { ProcedureFormSummaryComponent } from '../../procedure-form-summary';

@Component({
  selector: 'app-method-b-procedures-summary-template',
  imports: [GovukComponentsModule, ProcedureFormSummaryComponent, NgIf],
  templateUrl: './method-b-procedures-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MethodBProceduresSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: EmpMethodBProcedures;
}
