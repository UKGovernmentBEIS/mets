import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { VerificationBodyDetails } from 'pmrv-api';

@Component({
  selector: 'app-verification-body-details-info-template',
  imports: [SharedModule],
  templateUrl: './verification-body-details-info-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationBodyDetailsInfoTemplateComponent {
  @Input() data: VerificationBodyDetails;
}
