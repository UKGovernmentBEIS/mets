import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLinkWithHref } from '@angular/router';

import { existingControlContainer } from '@shared/providers/control-container.factory';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-verify-emission-reduction-claim-form',
  imports: [GovukComponentsModule, ReactiveFormsModule, RouterLinkWithHref],
  templateUrl: './verify-emission-reduction-claim-form.component.html',
  viewProviders: [existingControlContainer],
})
export class VerifyEmissionReductionClaimFormComponent {}
