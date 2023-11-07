import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { PermitVariationDetails } from 'pmrv-api';

import { significantChangesMonitoringMethodologyPlan } from '../about-variation';
import { nonSignificantChanges, significantChangesMonitoringPlan } from '../about-variation';

@Component({
  selector: 'app-about-variation-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent implements OnInit {
  @Input() hasBottomBorder = true;
  @Input() showChangeLink: boolean;
  @Input() permitVariationDetails: PermitVariationDetails;
  nonSignificantChangesText = [];
  significantChangesMonitoringPlanText = [];
  significantChangesMonitoringMethodologyPlanText = [];

  ngOnInit(): void {
    const modifications = this.permitVariationDetails?.modifications ?? [];

    this.nonSignificantChangesText = modifications
      ?.filter((change) => Object.keys(nonSignificantChanges).includes(change.type))
      .map((change) =>
        change.type === 'OTHER_NON_SIGNFICANT' ? change.otherSummary : nonSignificantChanges[change.type],
      );

    this.significantChangesMonitoringPlanText = modifications
      ?.filter((change) => Object.keys(significantChangesMonitoringPlan).includes(change.type))
      .map((change) =>
        change.type === 'OTHER_MONITORING_PLAN' ? change.otherSummary : significantChangesMonitoringPlan[change.type],
      );

    this.significantChangesMonitoringMethodologyPlanText = modifications
      ?.filter((change) => Object.keys(significantChangesMonitoringMethodologyPlan).includes(change.type))
      .map((change) =>
        change.type === 'OTHER_MONITORING_METHODOLOGY_PLAN'
          ? change.otherSummary
          : significantChangesMonitoringMethodologyPlan[change.type],
      );
  }

  permitVariationDetailsExists() {
    return this.permitVariationDetails !== undefined && Object.keys(this.permitVariationDetails)?.length > 0;
  }
}
