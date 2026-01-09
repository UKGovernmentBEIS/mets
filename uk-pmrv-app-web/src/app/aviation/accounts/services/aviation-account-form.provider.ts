import { Injectable } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { distinctUntilChanged, filter, map, Subject, switchMap, takeUntil } from 'rxjs';

import { getLocationOnShoreFormGroup } from '@aviation/shared/components/location-state-form/location-state-form.util';

import { GovukValidators } from 'govuk-components';

import { AviationAccountCreationDTO, AviationAccountsService, LocationOnShoreStateDTO } from 'pmrv-api';

export interface AviationAccountFormModel {
  name: FormControl<string | null>;
  emissionTradingScheme: FormControl<AviationAccountCreationDTO['emissionTradingScheme'] | null>;
  sopId: FormControl<number | null>;
  crcoCode: FormControl<string | null>;
  commencementDate: FormControl<string | null>;
  registryId?: FormControl<string | null>;
  id?: FormControl<number | null>;
  hasContactAddress?: FormControl<boolean[] | null>;
  location?: FormGroup<Record<keyof LocationOnShoreStateDTO, FormControl>>;
}

@Injectable()
export class AviationAccountFormProvider {
  private _form: FormGroup<AviationAccountFormModel>;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private accountsService: AviationAccountsService,
  ) {}

  resetForm(initialValue?: AviationAccountCreationDTO): void {
    if (this._form) {
      this._form.reset(initialValue);
    }
  }

  destroyForm(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get form(): FormGroup<AviationAccountFormModel> {
    return this._form ?? this.buildForm();
  }

  get formValue(): AviationAccountCreationDTO {
    return {
      ...this._form.value,
      sopId: +this._form.value.sopId || null,
    } as AviationAccountCreationDTO;
  }

  addFieldsForEdit() {
    this._form.addControl(
      'registryId',
      new FormControl(null, [GovukValidators.minMaxRangeNumberValidator(1000000, 9999999, 'Enter a 7 digit number')]),
    );
    this._form.addControl('id', new FormControl<number | null>(null));
    this._form.addControl('hasContactAddress', new FormControl<boolean[] | null>(null));
    this._form.addControl('location', getLocationOnShoreFormGroup());
  }

  private buildForm(): FormGroup<AviationAccountFormModel> {
    this._form = this.fb.group(
      {
        emissionTradingScheme: new FormControl<AviationAccountCreationDTO['emissionTradingScheme'] | null>(null, {
          validators: GovukValidators.required('Enter the emission trading scheme'),
        }),
        name: new FormControl<string | null>(null, {
          validators: GovukValidators.required('Enter the name of the aviation operator'),
        }),
        sopId: new FormControl<number | null>(null, {
          validators: [
            GovukValidators.max(9999999999, 'The ID should contain 10 numbers maximum'),
            GovukValidators.naturalNumber(),
          ],
        }),
        crcoCode: new FormControl<string | null>(null, {
          validators: GovukValidators.required('Enter the Central Route Charges Office number'),
        }),
        commencementDate: new FormControl<string | null>(null, {
          validators: GovukValidators.required('Enter the date of first known aviation activity'),
        }),
      },
      {
        updateOn: 'change',
      },
    );

    const nameCrcoEts$ = this._form.valueChanges.pipe(
      map(({ name, crcoCode, emissionTradingScheme, id }) => {
        return { name, crcoCode, emissionTradingScheme, id };
      }),
      takeUntil(this.destroy$),
    );

    nameCrcoEts$
      .pipe(
        filter(({ name, emissionTradingScheme }) => {
          return !!emissionTradingScheme && !!name;
        }),
        distinctUntilChanged((previous, current) => {
          return previous.name === current.name && previous.emissionTradingScheme === current.emissionTradingScheme;
        }),
        switchMap(({ name, emissionTradingScheme, id }) => {
          return this.accountsService.isExistingAccountName1(name, emissionTradingScheme, id);
        }),
      )
      .subscribe((exists) => {
        this.nameCtrl.setErrors(
          exists ? { name: 'The account name is already used. Please enter a different name' } : null,
        );
      });

    nameCrcoEts$
      .pipe(
        filter(({ crcoCode, emissionTradingScheme }) => {
          return !!emissionTradingScheme && !!crcoCode;
        }),
        distinctUntilChanged((previous, current) => {
          return (
            previous.crcoCode === current.crcoCode && previous.emissionTradingScheme === current.emissionTradingScheme
          );
        }),
        switchMap(({ crcoCode, emissionTradingScheme, id }) => {
          return this.accountsService.isExistingCrcoCode(crcoCode, emissionTradingScheme, id);
        }),
      )
      .subscribe((exists) => {
        this.crcoCtrl().setErrors(
          exists ? { crcoCode: 'CRCO number is already used. Please enter a different CRCO' } : null,
        );
      });

    return this._form;
  }

  private get nameCtrl(): AbstractControl {
    return this._form.get('name');
  }

  private crcoCtrl(): AbstractControl {
    return this._form.get('crcoCode');
  }
}
