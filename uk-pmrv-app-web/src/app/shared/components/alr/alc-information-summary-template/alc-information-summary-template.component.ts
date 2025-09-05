import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { ALRApplicationRegulatorReviewOutcome } from 'pmrv-api';

import { AllocationListTemplateComponent } from '../allocation-list-template/allocation-list-template.component';

@Component({
  selector: 'app-alr-alc-information-summary-template',
  templateUrl: './alc-information-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink, AllocationListTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAlcInformationSummaryTemplateComponent {
  @Input() data: ALRApplicationRegulatorReviewOutcome;
  @Input() editable: boolean;
}
