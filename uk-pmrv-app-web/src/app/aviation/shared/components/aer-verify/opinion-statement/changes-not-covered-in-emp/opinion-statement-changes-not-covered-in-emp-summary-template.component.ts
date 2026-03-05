import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerMonitoringPlanChanges } from 'pmrv-api';

@Component({
  selector: 'app-opinion-statement-changes-not-covered-in-emp-summary-template',
  templateUrl: './opinion-statement-changes-not-covered-in-emp-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent {
  @Input() aerMonitoringPlanChanges: AviationAerMonitoringPlanChanges;
  @Input() additionalChangesNotCovered: boolean;
  @Input() additionalChangesNotCoveredDetails: string;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
