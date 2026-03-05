import { Component, Input } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { TemporaryFactor } from 'pmrv-api';

import { existingControlContainer } from '../../../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-temporary-factor',
  templateUrl: './temporary-factor.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class TemporaryFactorComponent {
  @Input() today: Date;

  static controlsFactory(notification: TemporaryFactor, disabled = false): GroupBuilderConfig<TemporaryFactor> {
    return {
      description: new UntypedFormControl(
        {
          value: notification?.description ?? null,
          disabled,
        },
        [
          GovukValidators.required('Enter the factors preventing compliance'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ),
      startDateOfNonCompliance: new UntypedFormControl({
        value: notification?.startDateOfNonCompliance ? new Date(notification.startDateOfNonCompliance) : null,
        disabled,
      }),
      endDateOfNonCompliance: new UntypedFormControl({
        value: notification?.endDateOfNonCompliance ? new Date(notification.endDateOfNonCompliance) : null,
        disabled,
      }),
      inRespectOfMonitoringMethodology: new UntypedFormControl(
        {
          value: notification?.inRespectOfMonitoringMethodology ?? null,
          disabled,
        },
        [GovukValidators.required('Select yes or no')],
      ),
      details: new UntypedFormControl(
        {
          value: notification?.details ?? null,
          disabled,
        },
        [
          GovukValidators.required('Enter details of the interim monitoring and reporting methodology adopted'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ),
      proof: new UntypedFormControl(
        {
          value: notification?.proof ?? null,
          disabled,
        },
        [
          GovukValidators.required(
            'Enter proof of the necessity for a change to the monitoring and reporting methodology',
          ),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ),
      measures: new UntypedFormControl(
        {
          value: notification?.measures ?? null,
          disabled,
        },
        [
          GovukValidators.required('Enter the measures taken to ensure prompt restoration of compliance'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ),
    };
  }
}
