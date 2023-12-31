import { Component, Input } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { OtherFactor } from 'pmrv-api';

import { existingControlContainer } from '../../../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-other-factor',
  templateUrl: './other-factor.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class OtherFactorComponent {
  @Input() today: Date;

  reportingTypeOptions: OtherFactor['reportingType'][] = [
    'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT',
    'EXCEEDED_THRESHOLD_STATED_HSE_PERMIT',
    'RENOUNCE_FREE_ALLOCATIONS',
  ];

  static controlsFactory(notification: OtherFactor, disabled = false): GroupBuilderConfig<any> {
    return {
      description: new UntypedFormControl({ value: notification?.description ?? null, disabled }, [
        GovukValidators.required('Enter details'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      reportingType: new UntypedFormControl({ value: notification?.reportingType ?? null, disabled }, [
        GovukValidators.required('Select one'),
      ]),
      startDateOfNonCompliance_EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT: new UntypedFormControl({
        value: notification?.startDateOfNonCompliance ? new Date(notification.startDateOfNonCompliance) : null,
        disabled,
      }),
      startDateOfNonCompliance_EXCEEDED_THRESHOLD_STATED_HSE_PERMIT: new UntypedFormControl({
        value: notification?.startDateOfNonCompliance ? new Date(notification.startDateOfNonCompliance) : null,
        disabled,
      }),
      startDateOfNonCompliance_OTHER_ISSUE: new UntypedFormControl({
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
