import { Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { MonitoringApproachTypePipe } from '@aviation/shared/pipes/monitoring-approach-type.pipe';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmissionSmallEmittersSupportFacilityFormValues } from './monitoring-approach.interfaces';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-monitoring-approach-summary-template',
  templateUrl: './monitoring-approach-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, RouterLinkWithHref, MonitoringApproachTypePipe],
  viewProviders: [existingControlContainer],
})
export class MonitoringApproachSummaryTemplateComponent {
  @Input() data: EmissionSmallEmittersSupportFacilityFormValues | null;
  @Input() isEditable = false;
  @Input() changeUrlQueryParams: Params = {};
}
