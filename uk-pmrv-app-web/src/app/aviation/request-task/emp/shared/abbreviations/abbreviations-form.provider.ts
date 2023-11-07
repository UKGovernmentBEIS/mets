import { inject, Injectable } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { EmpAbbreviations } from 'pmrv-api';

import { TaskFormProvider } from '../../../task-form.provider';

export interface AbbreviationsFormModel {
  exist: FormControl<boolean | null>;
  abbreviationDefinitions?: FormArray<
    FormGroup<{
      abbreviation: FormControl<string | null>;
      definition: FormControl<string | null>;
    }>
  >;
}

@Injectable()
export class AbbreviationsFormProvider implements TaskFormProvider<EmpAbbreviations, AbbreviationsFormModel> {
  private fb = inject(FormBuilder);
  private _form: FormGroup<AbbreviationsFormModel>;
  private destroy$ = new Subject<void>();

  get form(): FormGroup<AbbreviationsFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  setFormValue(abbreviations: EmpAbbreviations | undefined) {
    if (abbreviations) {
      this.existCtrl.setValue(abbreviations.exist ?? null);
      if (abbreviations.abbreviationDefinitions?.length) {
        this.form.setControl(
          'abbreviationDefinitions',
          this.fb.array(abbreviations.abbreviationDefinitions.map(() => this.createDefinitionGroup())),
        );
      }

      this.form.setValue(abbreviations as any);
    }
  }

  getFormValue(): EmpAbbreviations {
    return this.form.value as EmpAbbreviations;
  }

  addAbbreviationDefinitionCtrl() {
    this.definitionsCtrl.push(this.createDefinitionGroup());
  }

  removeAbbreviationDefinitionCtrl(index: number) {
    if (this.definitionsCtrl.length > 1) {
      this.definitionsCtrl.removeAt(index);
    }
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        exist: new FormControl(null, {
          validators: [GovukValidators.required('Select yes or no')],
          updateOn: 'change',
        }),
      },
      { updateOn: 'change' },
    );

    this.existCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((hasAbbreviations) => {
      if (hasAbbreviations) {
        this.addDefinitionsCtrl();
      } else {
        this.form.removeControl('abbreviationDefinitions');
      }
    });
  }

  private addDefinitionsCtrl() {
    this.form.addControl('abbreviationDefinitions', this.fb.array([this.createDefinitionGroup()]));
  }

  private createDefinitionGroup(): FormGroup {
    return this.fb.group({
      abbreviation: [
        null,
        [
          GovukValidators.required('Enter an abbreviation or term used'),
          GovukValidators.maxLength(30, 'Enter up to 30 characters'),
        ],
      ],
      definition: [
        null,
        [GovukValidators.required('Enter a definition'), GovukValidators.maxLength(255, 'Enter up to 255 characters')],
      ],
    });
  }

  private get existCtrl(): AbstractControl {
    return this.form.get('exist');
  }

  private get definitionsCtrl(): FormArray {
    return this.form.get('abbreviationDefinitions') as FormArray;
  }
}
