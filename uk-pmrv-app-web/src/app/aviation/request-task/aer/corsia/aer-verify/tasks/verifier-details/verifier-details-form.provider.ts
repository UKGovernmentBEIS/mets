import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import {
  AviationAerCorsiaInterestConflictAvoidance,
  AviationAerCorsiaVerificationTeamLeader,
  AviationAerCorsiaVerifierDetails,
} from 'pmrv-api';

export interface AviationAerCorsiaVerificationTeamLeaderFormModel {
  name: FormControl<string | null>;
  position: FormControl<string | null>;
  role: FormControl<string | null>;
  email: FormControl<string | null>;
}

export interface AviationAerCorsiaInterestConflictAvoidanceFormModel {
  sixVerificationsConducted: FormControl<boolean | null>;
  breakTaken: FormControl<boolean | null>;
  reason: FormControl<string | null>;
  impartialityAssessmentResult: FormControl<string | null>;
}

export interface AviationAerCorsiaVerifierDetailsFormModel {
  verificationTeamLeader: FormGroup<AviationAerCorsiaVerificationTeamLeaderFormModel | null>;
  interestConflictAvoidance: FormGroup<AviationAerCorsiaInterestConflictAvoidanceFormModel | null>;
}

@Injectable()
export class VerifierDetailsFormProvider
  implements TaskFormProvider<AviationAerCorsiaVerifierDetails, AviationAerCorsiaVerifierDetailsFormModel>
{
  private _form: FormGroup<AviationAerCorsiaVerifierDetailsFormModel>;
  private destroy$ = new Subject<void>();
  private fb = inject(FormBuilder);

  destroyForm(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get form(): FormGroup<AviationAerCorsiaVerifierDetailsFormModel> {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get verificationTeamLeaderGroup(): FormGroup {
    return this.form.get('verificationTeamLeader') as FormGroup;
  }

  get interestConflictAvoidanceGroup(): FormGroup {
    return this.form.get('interestConflictAvoidance') as FormGroup;
  }

  getFormValue(): AviationAerCorsiaVerifierDetails {
    return {
      verificationTeamLeader: this.form.get('verificationTeamLeader').getRawValue(),
      interestConflictAvoidance: this.form.get('interestConflictAvoidance').getRawValue(),
    };
  }

  setFormValue(formValue: AviationAerCorsiaVerifierDetails): void {
    this.setVerificationTeamLeaderFormValue(formValue?.verificationTeamLeader ?? null);
    this.setInterestConflictAvoidanceFormValue(formValue?.interestConflictAvoidance ?? null);
  }

  setVerificationTeamLeaderFormValue(formValue: AviationAerCorsiaVerificationTeamLeader): void {
    this.verificationTeamLeaderGroup.setValue({
      name: formValue?.name ?? null,
      position: formValue?.position ?? null,
      role: formValue?.role ?? null,
      email: formValue?.email ?? null,
    });
  }

  setInterestConflictAvoidanceFormValue(formValue: AviationAerCorsiaInterestConflictAvoidance): void {
    this.interestConflictAvoidanceGroup.setValue({
      sixVerificationsConducted: formValue?.sixVerificationsConducted ?? null,
      breakTaken: formValue?.sixVerificationsConducted ? (formValue?.breakTaken ?? null) : null,
      reason: !formValue?.breakTaken ? (formValue?.reason ?? null) : null,
      impartialityAssessmentResult: formValue?.impartialityAssessmentResult ?? null,
    });
  }

  private _buildVerificationTeamLeaderGroup(): FormGroup<AviationAerCorsiaVerificationTeamLeaderFormModel> {
    return this.fb.group(
      {
        name: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the name of the verification team leader'),
            GovukValidators.maxLength(
              500,
              'The name of the verification team leader should not be more than 500 characters',
            ),
          ],
        }),
        position: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the position of the verification team leader'),
            GovukValidators.maxLength(
              500,
              'The position of the verification team leader should not be more than 500 characters',
            ),
          ],
        }),
        role: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the role and expertise of the verification team leader'),
            GovukValidators.maxLength(
              500,
              'The role and expertise of the verification team leader should not be more than 500 characters',
            ),
          ],
        }),
        email: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the email address of the verification team leader'),
            GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
            GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );
  }

  private _buildInterestConflictAvoidanceGroup(): FormGroup<AviationAerCorsiaInterestConflictAvoidanceFormModel> {
    return this.fb.group(
      {
        sixVerificationsConducted: new FormControl<boolean>(null, {
          validators: [
            GovukValidators.required('Select if the team leader has made more than 6 annual visits to this operator'),
          ],
        }),
        breakTaken: new FormControl<boolean>({ value: null, disabled: true }),
        reason: new FormControl<string>({ value: null, disabled: true }),
        impartialityAssessmentResult: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Provide a reason why you could not meet the conflict of interest requirement'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );
  }

  private _buildForm() {
    this._form = this.fb.group(
      {
        verificationTeamLeader: this._buildVerificationTeamLeaderGroup(),
        interestConflictAvoidance: this._buildInterestConflictAvoidanceGroup(),
      },
      { updateOn: 'change' },
    );

    this._form
      .get('interestConflictAvoidance')
      .get('sixVerificationsConducted')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value) {
          this._form.get('interestConflictAvoidance').get('breakTaken').enable();
        } else {
          this._form.get('interestConflictAvoidance').get('breakTaken').disable();
          this._form.get('interestConflictAvoidance').get('breakTaken').setValue(null);
          this._form.get('interestConflictAvoidance').get('reason').disable();
          this._form.get('interestConflictAvoidance').get('reason').setValue(null);
        }
      });

    this._form
      .get('interestConflictAvoidance')
      .get('breakTaken')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value) {
          this._form.get('interestConflictAvoidance').get('reason').disable();
          this._form.get('interestConflictAvoidance').get('reason').setValue(null);
        } else if (value === false) {
          this._form.get('interestConflictAvoidance').get('reason').enable();
        }
      });
    this._setDynamicValidators();
  }

  private _setDynamicValidators() {
    const interestConflictAvoidance = this._form.get('interestConflictAvoidance');
    const sixVerificationsConducted = interestConflictAvoidance.get('sixVerificationsConducted');
    const breakTaken = interestConflictAvoidance.get('breakTaken');
    const reason = interestConflictAvoidance.get('reason');

    sixVerificationsConducted.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (value) {
        breakTaken.enable();
        breakTaken.setValidators([
          GovukValidators.required(
            'Select if you then took a break of three consecutive years from providing verifications for this operator',
          ),
        ]);
      } else {
        breakTaken.disable();
        breakTaken.clearValidators();
        breakTaken.setValue(null);
        reason.disable();
        reason.clearValidators();
        reason.setValue(null);
      }
      breakTaken.updateValueAndValidity();
      reason.updateValueAndValidity();
    });

    breakTaken.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (value === false) {
        reason.enable();
        reason.setValidators([
          GovukValidators.required('Provide a reason why you could not meet the conflict of interest requirement'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]);
      } else {
        reason.disable();
        reason.clearValidators();
        reason.setValue(null);
      }
      reason.updateValueAndValidity();
    });
  }
}
