import { Injectable } from '@angular/core';
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

  constructor(private fb: FormBuilder) {}

  destroyForm(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get form(): FormGroup<AviationAerCorsiaVerifierDetailsFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
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
      breakTaken: formValue?.sixVerificationsConducted ? formValue?.breakTaken ?? null : null,
      reason: !formValue?.breakTaken ? formValue?.reason ?? null : null,
      impartialityAssessmentResult: !formValue?.sixVerificationsConducted
        ? formValue?.impartialityAssessmentResult ?? null
        : null,
    });
  }

  get verificationTeamLeaderGroup(): FormGroup {
    return this.form.get('verificationTeamLeader') as FormGroup;
  }

  get interestConflictAvoidanceGroup(): FormGroup {
    return this.form.get('interestConflictAvoidance') as FormGroup;
  }

  private buildVerificationTeamLeaderGroup(): FormGroup<AviationAerCorsiaVerificationTeamLeaderFormModel> {
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

  private buildInterestConflictAvoidanceGroup(): FormGroup<AviationAerCorsiaInterestConflictAvoidanceFormModel> {
    return this.fb.group(
      {
        sixVerificationsConducted: new FormControl<boolean>(null, {
          validators: [
            GovukValidators.required('Select if the team leader has made more than 6 annual visits to this operator'),
          ],
        }),
        breakTaken: new FormControl<boolean>(null, {
          validators: [
            GovukValidators.required(
              'Select if you then took a break of three consecutive years from providing verifications for this operator',
            ),
          ],
        }),
        reason: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Provide a reason why you could not meet the conflict of interest requirement'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
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

  private buildForm() {
    this._form = this.fb.group(
      {
        verificationTeamLeader: this.buildVerificationTeamLeaderGroup(),
        interestConflictAvoidance: this.buildInterestConflictAvoidanceGroup(),
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
          this._form.get('interestConflictAvoidance').get('impartialityAssessmentResult').disable();
          this._form.get('interestConflictAvoidance').get('impartialityAssessmentResult').setValue(null);
        } else {
          this._form.get('interestConflictAvoidance').get('impartialityAssessmentResult').enable();
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
        } else {
          this._form.get('interestConflictAvoidance').get('reason').enable();
        }
      });
  }
}
