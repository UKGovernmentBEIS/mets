import { NgFor, NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpManagementProceduresCorsia } from 'pmrv-api';

@Component({
  selector: 'app-management-procedures-summary-template',
  templateUrl: './management-procedures-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, RouterLinkWithHref, NgFor, NgForOf],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: EmpManagementProceduresCorsia;
  @Input() dataFlowDiagramFile: { fileName: string; downloadUrl: string }[] = [];
}
