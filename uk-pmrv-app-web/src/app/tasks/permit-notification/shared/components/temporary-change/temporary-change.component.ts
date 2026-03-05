import { Component, Input } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { TemporaryChange } from 'pmrv-api';

import { existingControlContainer } from '../../../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-temporary-change',
  templateUrl: './temporary-change.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class TemporaryChangeComponent {
  @Input() today: Date;

  static controlsFactory(notification: TemporaryChange, disabled = false): GroupBuilderConfig<TemporaryChange> {
    return {
      description: new UntypedFormControl({ value: notification?.description ?? null, disabled }, [
        GovukValidators.required('Enter a description for the temporary change'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      justification: new UntypedFormControl({ value: notification?.justification ?? null, disabled }, [
        GovukValidators.required('Enter a justification for not submitting a permit variation'),
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
