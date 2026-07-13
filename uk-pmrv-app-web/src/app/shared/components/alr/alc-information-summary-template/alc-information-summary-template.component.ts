import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { ALRApplicationRegulatorReviewOutcome } from 'pmrv-api';

import { AllocationListTemplateComponent } from '../allocation-list-template/allocation-list-template.component';

@Component({
  selector: 'app-alr-alc-information-summary-template',
  imports: [SharedModule, RouterLink, AllocationListTemplateComponent],
  templateUrl: './alc-information-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAlcInformationSummaryTemplateComponent {
  @Input() data: ALRApplicationRegulatorReviewOutcome;
  @Input() editable: boolean;
  @Input() year: number;
}
