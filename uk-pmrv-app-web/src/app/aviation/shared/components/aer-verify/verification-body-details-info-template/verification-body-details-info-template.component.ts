import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { VerificationBodyDetails } from 'pmrv-api';

@Component({
  selector: 'app-verification-body-details-info-template',
  templateUrl: './verification-body-details-info-template.component.html',
  standalone: true,
  imports: [SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationBodyDetailsInfoTemplateComponent {
  @Input() data: VerificationBodyDetails;
}
