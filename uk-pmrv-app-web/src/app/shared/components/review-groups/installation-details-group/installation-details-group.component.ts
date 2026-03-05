import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import {
  AerApplicationReviewRequestTaskPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
  AerApplicationVerificationSubmittedRequestActionPayload,
} from 'pmrv-api';

@Component({
  selector: 'app-installation-details-group',
  templateUrl: './installation-details-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationDetailsGroupComponent {
  @Input() payload:
    | AerApplicationVerificationSubmitRequestTaskPayload
    | AerApplicationReviewRequestTaskPayload
    | AerApplicationVerificationSubmittedRequestActionPayload;
}
