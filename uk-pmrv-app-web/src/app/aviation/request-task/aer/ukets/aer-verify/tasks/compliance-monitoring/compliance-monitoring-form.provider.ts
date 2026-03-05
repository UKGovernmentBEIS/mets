import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerComplianceMonitoringReportingRules } from 'pmrv-api';

export interface AviationAerUketsComplianceMonitoringFormModel {
  accuracyCompliant: FormControl<boolean | null>;
  accuracyNonCompliantReason: FormControl<string | null>;
  completenessCompliant: FormControl<boolean | null>;
  completenessNonCompliantReason: FormControl<string | null>;
  consistencyCompliant: FormControl<boolean | null>;
  consistencyNonCompliantReason: FormControl<string | null>;
  comparabilityCompliant: FormControl<boolean | null>;
  comparabilityNonCompliantReason: FormControl<string | null>;
  transparencyCompliant: FormControl<boolean | null>;
  transparencyNonCompliantReason: FormControl<string | null>;
  integrityCompliant: FormControl<boolean | null>;
  integrityNonCompliantReason: FormControl<string | null>;
}

@Injectable()
export class ComplianceMonitoringFormProvider
  implements
    TaskFormProvider<AviationAerComplianceMonitoringReportingRules, AviationAerUketsComplianceMonitoringFormModel>
{
  private store = inject(RequestTaskStore);
  private fb = inject(FormBuilder);
  private _form: FormGroup<AviationAerUketsComplianceMonitoringFormModel>;
  private destroy$ = new Subject<void>();

  get form() {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(
    aviationAerComplianceMonitoringReportingRules: AviationAerComplianceMonitoringReportingRules | undefined,
  ) {
    if (aviationAerComplianceMonitoringReportingRules) {
      this.form.patchValue(aviationAerComplianceMonitoringReportingRules);
    }
  }

  getFormValue(): AviationAerComplianceMonitoringReportingRules {
    return this.form.value as AviationAerComplianceMonitoringReportingRules;
  }

  private _buildForm() {
    const formGroup = this.createComplianceForm();

    return (this._form = formGroup);
  }

  createComplianceForm() {
    const formGroup = this.fb.group(
      {
        accuracyCompliant: new FormControl<boolean | null>(null, [
          GovukValidators.required('Select if the aircraft operator complied with the principle of accuracy'),
        ]),
        accuracyNonCompliantReason: new FormControl<string | null>(null),
        completenessCompliant: new FormControl<boolean | null>(null, [
          GovukValidators.required('Select if the aircraft operator complied with the principle of completeness'),
        ]),
        completenessNonCompliantReason: new FormControl<string | null>(null),
        consistencyCompliant: new FormControl<boolean | null>(null, [
          GovukValidators.required('Select if the aircraft operator complied with the principle of consistency'),
        ]),
        consistencyNonCompliantReason: new FormControl<string | null>(null),
        comparabilityCompliant: new FormControl<boolean | null>(null, [
          GovukValidators.required(
            'Select if the aircraft operator complied with the principle of comparability over time',
          ),
        ]),
        comparabilityNonCompliantReason: new FormControl<string | null>(null),
        transparencyCompliant: new FormControl<boolean | null>(null, [
          GovukValidators.required('Select if the aircraft operator complied with the principle of transparency'),
        ]),
        transparencyNonCompliantReason: new FormControl<string | null>(null),
        integrityCompliant: new FormControl<boolean | null>(null, [
          GovukValidators.required(
            'Select if the aircraft operator complied with the principle of integrity of methodology',
          ),
        ]),
        integrityNonCompliantReason: new FormControl<string | null>(null),
      },
      { updateOn: 'change' },
    );

    const accuracyCompliant = formGroup.get('accuracyCompliant');
    const accuracyNonCompliantReason = formGroup.get('accuracyNonCompliantReason');

    accuracyCompliant.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (!value) {
        accuracyNonCompliantReason.setValidators([
          GovukValidators.required(
            'Provide a reason why the operator was not compliant with the principle of accuracy',
          ),
          GovukValidators.maxLength(10000, 'The reason should not be more than 10000 characters'),
        ]);
      } else {
        accuracyNonCompliantReason.clearValidators();
        accuracyNonCompliantReason.setValue(null);
      }
      accuracyNonCompliantReason.updateValueAndValidity();
    });

    const completenessCompliant = formGroup.get('completenessCompliant');
    const completenessNonCompliantReason = formGroup.get('completenessNonCompliantReason');

    completenessCompliant.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (!value) {
        completenessNonCompliantReason.setValidators([
          GovukValidators.required(
            'Provide a reason why the operator was not compliant with the principle of completeness',
          ),
          GovukValidators.maxLength(10000, 'The reason should not be more than 10000 characters'),
        ]);
      } else {
        completenessNonCompliantReason.clearValidators();
        completenessNonCompliantReason.setValue(null);
      }
      completenessNonCompliantReason.updateValueAndValidity();
    });

    const consistencyCompliant = formGroup.get('consistencyCompliant');
    const consistencyNonCompliantReason = formGroup.get('consistencyNonCompliantReason');

    consistencyCompliant.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (!value) {
        consistencyNonCompliantReason.setValidators([
          GovukValidators.required(
            'Provide a reason why the operator was not compliant with the principle of consistency',
          ),
          GovukValidators.maxLength(10000, 'The reason should not be more than 10000 characters'),
        ]);
      } else {
        consistencyNonCompliantReason.clearValidators();
        consistencyNonCompliantReason.setValue(null);
      }
      consistencyNonCompliantReason.updateValueAndValidity();
    });

    const comparabilityCompliant = formGroup.get('comparabilityCompliant');
    const comparabilityNonCompliantReason = formGroup.get('comparabilityNonCompliantReason');

    comparabilityCompliant.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (!value) {
        comparabilityNonCompliantReason.setValidators([
          GovukValidators.required(
            'Provide a reason why the operator was not compliant with the principle of comparability over time',
          ),
          GovukValidators.maxLength(10000, 'The reason should not be more than 10000 characters'),
        ]);
      } else {
        comparabilityNonCompliantReason.clearValidators();
        comparabilityNonCompliantReason.setValue(null);
      }
      comparabilityNonCompliantReason.updateValueAndValidity();
    });

    const transparencyCompliant = formGroup.get('transparencyCompliant');
    const transparencyNonCompliantReason = formGroup.get('transparencyNonCompliantReason');

    transparencyCompliant.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (!value) {
        transparencyNonCompliantReason.setValidators([
          GovukValidators.required(
            'Provide a reason why the operator was not compliant with the principle of transparency',
          ),
          GovukValidators.maxLength(10000, 'The reason should not be more than 10000 characters'),
        ]);
      } else {
        transparencyNonCompliantReason.clearValidators();
        transparencyNonCompliantReason.setValue(null);
      }
      transparencyNonCompliantReason.updateValueAndValidity();
    });

    const integrityCompliant = formGroup.get('integrityCompliant');
    const integrityNonCompliantReason = formGroup.get('integrityNonCompliantReason');

    integrityCompliant.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (!value) {
        integrityNonCompliantReason.setValidators([
          GovukValidators.required(
            'Provide a reason why the operator was not compliant with the principle of integrity of methodology',
          ),
          GovukValidators.maxLength(10000, 'The reason should not be more than 10000 characters'),
        ]);
      } else {
        integrityNonCompliantReason.clearValidators();
        integrityNonCompliantReason.setValue(null);
      }
      integrityNonCompliantReason.updateValueAndValidity();
    });

    return formGroup;
  }
}
