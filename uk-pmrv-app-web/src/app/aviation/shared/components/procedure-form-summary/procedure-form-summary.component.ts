import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { GovukComponentsModule } from 'govuk-components';

import { EmpProcedureForm } from 'pmrv-api';

@Component({
  selector: 'app-procedure-form-summary',
  standalone: true,
  imports: [NgIf, RouterLinkWithHref, GovukComponentsModule],
  templateUrl: './procedure-form-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcedureFormSummaryComponent {
  @Input() isEditable = false;
  @Input() header: string;
  @Input() procedureFormData: EmpProcedureForm;
  @Input() formRouterLink: string;
}
