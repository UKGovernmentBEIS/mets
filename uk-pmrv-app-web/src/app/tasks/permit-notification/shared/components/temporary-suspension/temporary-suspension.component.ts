import { Component, Input } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { TemporarySuspension } from 'pmrv-api';

import { existingControlContainer } from '../../../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-temporary-suspension',
  templateUrl: './temporary-suspension.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class TemporarySuspensionComponent {
  @Input() today: Date;

  static controlsFactory(notification: TemporarySuspension, disabled = false): GroupBuilderConfig<TemporarySuspension> {
    return {
      description: new UntypedFormControl({ value: notification?.description ?? null, disabled }, [
        GovukValidators.required('Enter the regulated activities which are temporarily suspended'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      startDateOfNonCompliance: new UntypedFormControl({
        value: notification?.startDateOfNonCompliance ? new Date(notification.startDateOfNonCompliance) : null,
        disabled,
      }),
      endDateOfNonCompliance: new UntypedFormControl({
        value: notification?.endDateOfNonCompliance ? new Date(notification.endDateOfNonCompliance) : null,
        disabled,
      }),
    };
  }
}
