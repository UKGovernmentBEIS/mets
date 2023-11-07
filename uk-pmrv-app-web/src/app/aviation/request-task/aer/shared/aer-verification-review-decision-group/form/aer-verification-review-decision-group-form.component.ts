import { Component } from '@angular/core';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-aer-verification-review-decision-group-form',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './aer-verification-review-decision-group-form.component.html',
  viewProviders: [existingControlContainer],
})
export class AerVerificationReviewDecisionGroupFormComponent {}
