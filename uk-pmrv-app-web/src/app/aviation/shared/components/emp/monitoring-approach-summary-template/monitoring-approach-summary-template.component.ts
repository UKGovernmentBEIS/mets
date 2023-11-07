import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink, RouterLinkWithHref } from '@angular/router';

import { MonitoringApproachTypePipe } from '@aviation/shared/pipes/monitoring-approach-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

import { ProcedureFormSummaryComponent } from '../../procedure-form-summary';
import { SimplifiedMonitoringApproach } from './monitoring-approach-types.interface';

type EmissionsMonitoringApproachFormValues = {
  monitoringApproachType: EmpEmissionsMonitoringApproach['monitoringApproachType'];
  simplifiedApproach?: SimplifiedMonitoringApproach;
};

@Component({
  selector: 'app-monitoring-approach-summary-template',
  standalone: true,
  imports: [
    GovukComponentsModule,
    RouterLinkWithHref,
    ProcedureFormSummaryComponent,
    RouterLink,
    NgIf,
    MonitoringApproachTypePipe,
    SharedModule,
  ],
  templateUrl: './monitoring-approach-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: EmissionsMonitoringApproachFormValues;
  @Input() files: { downloadUrl: string; fileName: string }[];
  @Input() changeUrlQueryParams: Params = {};
}
