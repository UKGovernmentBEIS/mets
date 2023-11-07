import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ValidationErrors } from '@angular/forms';

import { AerRegulatedActivity } from 'pmrv-api';

@Component({
  selector: 'app-aer-regulated-activities-summary-template',
  templateUrl: './aer-regulated-activities-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./aer-regulated-activities-summary-template.component.scss'],
})
export class AerRegulatedActivitiesSummaryTemplateComponent {
  @Input() cssClass: string;
  @Input() noBottomBorder = false;
  @Input() activity: AerRegulatedActivity;
  @Input() errors: ValidationErrors | null;
  @Input() hasError: boolean;
  @Input() isEditable: boolean;

  missingCrfCode(): boolean {
    return (
      !this.hasAtLeastOneCrfCode() ||
      (this.activity.hasIndustrialCrf && !this.activity.industrialCrf) ||
      (this.activity.hasEnergyCrf && !this.activity.energyCrf)
    );
  }

  hasAtLeastOneCrfCode(): boolean {
    return !!this.activity.energyCrf || !!this.activity.industrialCrf;
  }
}
