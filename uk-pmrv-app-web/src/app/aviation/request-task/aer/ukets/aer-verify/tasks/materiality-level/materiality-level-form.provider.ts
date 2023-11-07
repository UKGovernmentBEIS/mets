import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { AviationAerMaterialityLevelFormModel } from '@aviation/request-task/aer/ukets/aer-verify/tasks/materiality-level/materiality-level.interface';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerMaterialityLevel } from 'pmrv-api';

import {
  aviationAerUkeEtsForAccreditedVerifiers,
  aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders,
  aviationAerUkeEtsRulesUkEts,
  aviationAerUkeEtsRulesUkEtsOther,
} from './reference-documents-type';

@Injectable()
export class MaterialityLevelFormProvider
  implements TaskFormProvider<AviationAerMaterialityLevel, AviationAerMaterialityLevelFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get form() {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get materialityDetailsCtrl() {
    return this.form.get('materialityDetails');
  }

  get accreditationReferenceDocumentTypesGroup() {
    return this.form.get('accreditationReferenceDocumentTypesGroup') as FormGroup;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(materialityLevel: AviationAerMaterialityLevel | undefined) {
    if (materialityLevel) {
      this.materialityDetailsCtrl.setValue(materialityLevel.materialityDetails);

      if (materialityLevel?.accreditationReferenceDocumentTypes?.length) {
        this.accreditationReferenceDocumentTypesGroup
          ?.get('aviationAerUkeEtsForAccreditedVerifiers')
          .setValue(
            materialityLevel.accreditationReferenceDocumentTypes.filter((type) =>
              aviationAerUkeEtsForAccreditedVerifiers.includes(type),
            ),
          );

        this.accreditationReferenceDocumentTypesGroup
          ?.get('aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders')
          .setValue(
            materialityLevel.accreditationReferenceDocumentTypes.filter((type) =>
              aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders.includes(type),
            ),
          );

        this.accreditationReferenceDocumentTypesGroup
          ?.get('aviationAerUkeEtsRulesUkEts')
          .setValue(
            materialityLevel.accreditationReferenceDocumentTypes.filter((type) =>
              aviationAerUkeEtsRulesUkEts.includes(type),
            ),
          );

        this.accreditationReferenceDocumentTypesGroup
          ?.get('aviationAerUkeEtsRulesUkEtsOther')
          .setValue(
            materialityLevel.accreditationReferenceDocumentTypes.filter((type) =>
              aviationAerUkeEtsRulesUkEtsOther.includes(type),
            ),
          );
      }

      if (materialityLevel.otherReference) {
        this.accreditationReferenceDocumentTypesGroup
          ?.get('aviationAerUkeEtsRulesUkEtsOther')
          .setValue(
            materialityLevel.accreditationReferenceDocumentTypes.filter((type) =>
              aviationAerUkeEtsRulesUkEtsOther.includes(type),
            ),
          );

        this.accreditationReferenceDocumentTypesGroup?.get('otherReference').setValue(materialityLevel.otherReference);
      }
    }
  }

  getFormValue(): AviationAerMaterialityLevel {
    const accreditationReferenceDocumentTypes = [
      ...(this.accreditationReferenceDocumentTypesGroup?.value.aviationAerUkeEtsForAccreditedVerifiers ?? []),
      ...(this.accreditationReferenceDocumentTypesGroup?.value
        .aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders ?? []),
      ...(this.accreditationReferenceDocumentTypesGroup?.value.aviationAerUkeEtsRulesUkEts ?? []),
      ...(this.accreditationReferenceDocumentTypesGroup?.value.aviationAerUkeEtsRulesUkEtsOther ?? []),
    ];

    const value: any = {
      materialityDetails: this.materialityDetailsCtrl?.value ?? null,
    };

    if (accreditationReferenceDocumentTypes.length > 0) {
      value.accreditationReferenceDocumentTypes = accreditationReferenceDocumentTypes;
    }

    if (accreditationReferenceDocumentTypes.includes('OTHER')) {
      value.otherReference = this.accreditationReferenceDocumentTypesGroup?.value.otherReference ?? null;
    }

    return value;
  }

  addaccreditationReferenceDocumentTypesGroup() {
    if (!this.form.contains('accreditationReferenceDocumentTypesGroup')) {
      this.form.addControl(
        'accreditationReferenceDocumentTypesGroup',
        this.createAccreditationReferenceDocumentTypesGroup(),
      );
    }
  }

  createAccreditationReferenceDocumentTypesGroup() {
    const otherReferenceValidator = [
      GovukValidators.required(`Enter reference details`),
      GovukValidators.maxLength(10000, `The details should not be more than 10000 characters`),
    ];

    const group = this.fb.group(
      {
        aviationAerUkeEtsForAccreditedVerifiers: [null],
        aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders: [null],
        aviationAerUkeEtsRulesUkEts: [null],
        aviationAerUkeEtsRulesUkEtsOther: [null],
        otherReference: [null],
      },
      { updateOn: 'change', validators: this._atLeastOneTypeSelectedValidator() },
    );

    group
      .get('aviationAerUkeEtsRulesUkEtsOther')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value[0] === 'OTHER') {
          group.get('otherReference').setValidators(otherReferenceValidator);
          group.get('otherReference').updateValueAndValidity();
        } else {
          group.get('otherReference').reset();
          group.get('otherReference').clearValidators();
        }
      });

    return group;
  }

  removeAccreditationReferenceDocumentTypesGroup() {
    if (this.form.contains('accreditationReferenceDocumentTypesGroup')) {
      this.form.removeControl('accreditationReferenceDocumentTypesGroup');
    }
  }

  private _buildForm() {
    this._form = this.fb.group({
      materialityDetails: new FormControl<AviationAerMaterialityLevel['materialityDetails'] | null>(null, [
        GovukValidators.required(
          'Provide details around the materiality level which the report has been assessed against',
        ),
        GovukValidators.maxLength(10000, 'The description should not be more than 10000 characters'),
      ]),
    });

    this.addaccreditationReferenceDocumentTypesGroup();
  }

  private _atLeastOneTypeSelectedValidator = (): ValidatorFn => {
    return (group: UntypedFormGroup): ValidationErrors => {
      const isValid =
        group.get('aviationAerUkeEtsForAccreditedVerifiers').value?.length ||
        group.get('aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders').value?.length ||
        group.get('aviationAerUkeEtsRulesUkEts').value?.length ||
        group.get('aviationAerUkeEtsRulesUkEtsOther').value?.length;
      return !isValid ? { invalidForm: 'You must select at least one document from the list' } : null;
    };
  };
}
