import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerMonitoringPlanChanges } from 'pmrv-api';

@Component({
  selector: 'app-opinion-statement-changes-not-covered-in-emp-summary-template',
  imports: [SharedModule, RouterLinkWithHref],
  templateUrl: './opinion-statement-changes-not-covered-in-emp-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent {
  @Input() aerMonitoringPlanChanges: AviationAerMonitoringPlanChanges;
  @Input() additionalChangesNotCovered: boolean;
  @Input() additionalChangesNotCoveredDetails: string;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
