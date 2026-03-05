import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { MonitoringApproachTypePipe } from '@aviation/shared/pipes/monitoring-approach-type.pipe';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-opinion-statement-emission-details-summary-template',
  templateUrl: './opinion-statement-emission-details-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, RouterLinkWithHref, MonitoringApproachTypePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementEmissionDetailsSummaryTemplateComponent {
  @Input() fuelTypes: { id: string; key: string; value: string }[];
  @Input() monitoringApproachType:
    | 'EUROCONTROL_SUPPORT_FACILITY'
    | 'EUROCONTROL_SMALL_EMITTERS'
    | 'FUEL_USE_MONITORING';
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
