import { Component, Injector, Input, OnInit, runInInjectionContext, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormGroup, UntypedFormControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { GroupBuilderConfig } from '@shared/types';
import { todayOrPastDateValidator } from '@tasks/permanent-cessation/submit/details-of-cessation/details-of-cessation-form.provider';
import { format } from 'date-fns';

import { GovukValidators } from 'govuk-components';

import { CessationNotification } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-permanent-cessation',
  templateUrl: './permanent-cessation.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class PermanentCessationComponent implements OnInit {
  @Input() today: Date;
  @Input() form!: FormGroup;

  isTemporary: Signal<boolean>;

  constructor(private readonly injector: Injector) {}

  ngOnInit(): void {
    const isTemporaryControl = this.form.get('notification.isTemporary');

    if (isTemporaryControl) {
      this.isTemporary = runInInjectionContext(this.injector, () =>
        toSignal(isTemporaryControl.valueChanges, { initialValue: isTemporaryControl.value }),
      );
    }

    isTemporaryControl?.valueChanges.subscribe((value) => {
      if (value === true) {
        this.form.get('notification.endDateOfNonCompliance').enable();
        this.form.get('notification.technicalCapabilityDetails.technicalCapability').enable();
        this.form
          .get('notification.technicalCapabilityDetails.technicalCapability')
          .setValidators([
            GovukValidators.required(
              'Select if the installation is technically capable of resuming regulated activities or technical capability needs to be restored',
            ),
          ]);
        this.form.get('notification.endDateOfNonCompliance').setValidators([todayOrFutureDateValidator()]);
        this.form.get('notification.endDateOfNonCompliance').updateValueAndValidity();
        this.form.get('notification.technicalCapabilityDetails.technicalCapability').updateValueAndValidity();
        this.form.get('notification.technicalCapabilityDetails.technicalCapability').markAsTouched();
        this.form
          .get('notification.technicalCapabilityDetails.RESUME_REG_ACTIVITIES_WITHOUT_PHYSICAL_CHANGES_details')
          .setValidators(GovukValidators.required('Enter a comment'));
        this.form
          .get('notification.technicalCapabilityDetails.RESTORE_TECHNICAL_CAPABILITY_TO_RESUME_REG_ACTIVITIES_details')
          .setValidators(GovukValidators.required('Enter a comment'));
      } else {
        this.form.get('notification.endDateOfNonCompliance').disable();
        this.form.get('notification.endDateOfNonCompliance').clearValidators();
        this.form.get('notification.endDateOfNonCompliance').setValue(null);
        this.form.get('notification.endDateOfNonCompliance').markAsUntouched();
        this.form.get('notification.endDateOfNonCompliance').markAsPristine();
        this.form.get('notification.endDateOfNonCompliance').updateValueAndValidity();
        this.form.get('notification.technicalCapabilityDetails.technicalCapability').setValue(null);
        this.form.get('notification.technicalCapabilityDetails.technicalCapability').markAsUntouched();
        this.form.get('notification.technicalCapabilityDetails.technicalCapability').markAsPristine();
        this.form.get('notification.technicalCapabilityDetails.technicalCapability').disable();
        this.form.get('notification.technicalCapabilityDetails.technicalCapability').clearValidators();
        this.form.get('notification.technicalCapabilityDetails.technicalCapability').updateValueAndValidity();
        this.form
          .get('notification.technicalCapabilityDetails.RESUME_REG_ACTIVITIES_WITHOUT_PHYSICAL_CHANGES_details')
          .clearValidators();
        this.form
          .get('notification.technicalCapabilityDetails.RESTORE_TECHNICAL_CAPABILITY_TO_RESUME_REG_ACTIVITIES_details')
          .clearValidators();
      }
    });

    isTemporaryControl.updateValueAndValidity({ emitEvent: true });
  }

  static controlsFactory(
    notification: CessationNotification,
    disabled = false,
  ): GroupBuilderConfig<CessationNotification> {
    const techDetails = [
      'RESUME_REG_ACTIVITIES_WITHOUT_PHYSICAL_CHANGES',
      'RESTORE_TECHNICAL_CAPABILITY_TO_RESUME_REG_ACTIVITIES',
    ].reduce((acc, value) => {
      acc[`${value}_details`] = new UntypedFormControl({
        value:
          notification?.technicalCapabilityDetails?.technicalCapability === value
            ? notification?.technicalCapabilityDetails?.details
            : null,
        disabled,
      });
      return acc;
    }, {});

    return {
      description: new UntypedFormControl({ value: notification?.description ?? null, disabled }, [
        GovukValidators.required('Enter a description of the cessation'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      startDateOfNonCompliance: new UntypedFormControl(
        {
          value: notification?.startDateOfNonCompliance ? new Date(notification?.startDateOfNonCompliance) : null,
          disabled,
        },
        {
          validators: [todayOrPastDateValidator()],
        },
      ),
      isTemporary: new UntypedFormControl(
        {
          value: notification?.isTemporary ?? null,
          disabled,
        },
        { updateOn: 'change', validators: GovukValidators.required('Select yes if you intend to resume operations') },
      ),
      endDateOfNonCompliance: new UntypedFormControl(
        {
          value: notification?.endDateOfNonCompliance ? new Date(notification?.endDateOfNonCompliance) : null,
          disabled,
        },
        {
          validators: [todayOrFutureDateValidator()],
        },
      ),
      technicalCapabilityDetails: new UntypedFormGroup(
        {
          technicalCapability: new UntypedFormControl(
            {
              value: notification?.technicalCapabilityDetails?.technicalCapability ?? null,
              disabled,
            },
            {
              validators: GovukValidators.required(
                'Select if the installation is technically capable of resuming regulated activities or technical capability needs to be restored',
              ),
            },
          ),
          ...techDetails,
        },
        { updateOn: 'change' },
      ),
    };
  }
}

export function todayOrFutureDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    const inputDate = format(control.value ?? new Date(), 'yyyy-MM-dd');
    const currentDate = format(new Date(), 'yyyy-MM-dd');
    const isTodayOrFuture = inputDate >= currentDate;
    return control.value && !isTodayOrFuture
      ? { invalidDate: 'Date activities are expected to resume must be today or in the future' }
      : null;
  };
}
