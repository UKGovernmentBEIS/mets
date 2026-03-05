import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { ProcedureFormSummaryComponent } from '@aviation/shared/components/procedure-form-summary';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpManagementProcedures } from 'pmrv-api';

@Component({
  selector: 'app-management-procedures-summary-template',
  templateUrl: './management-procedures-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, RouterLinkWithHref, ProcedureFormSummaryComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: EmpManagementProcedures;
  @Input() diagramAttachmentFiles: { fileName: string; downloadUrl: string }[] = [];
  @Input() riskAssessmentFiles: { fileName: string; downloadUrl: string }[] = [];
}
