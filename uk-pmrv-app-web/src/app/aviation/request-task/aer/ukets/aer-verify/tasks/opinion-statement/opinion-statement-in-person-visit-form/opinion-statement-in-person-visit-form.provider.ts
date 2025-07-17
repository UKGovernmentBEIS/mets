import { inject, Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject } from 'rxjs';

import { TASK_FORM_PROVIDER, TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { todayOrPastDateValidator } from '@tasks/aer/verification-submit/opinion-statement/validators/today-or-past-date.validator';

import { GovukValidators } from 'govuk-components';

import { AviationAerInPersonSiteVisit } from 'pmrv-api';

import {
  AviationAerInPersonSiteVisitFormModel,
  OpinionStatementFormProvider,
} from '../opinion-statement-form.provider';

@Injectable()
export class OpinionStatementInPersonVisitFormProvider
  implements TaskFormProvider<AviationAerInPersonSiteVisit, AviationAerInPersonSiteVisitFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private parentFormProvider = inject<OpinionStatementFormProvider>(TASK_FORM_PROVIDER);

  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  get visitDatesCtrl() {
    return this.form.get('visitDates') as FormArray;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(siteVisit: AviationAerInPersonSiteVisit | undefined): void {
    if (siteVisit) {
      const value = {
        ...siteVisit,
        visitDates: this._visitDatesFormatted(siteVisit.visitDates) ?? [],
      };

      this.form.patchValue(value);
    }
  }

  getFormValue(): AviationAerInPersonSiteVisit {
    const value = { ...this.form.value };

    return value as AviationAerInPersonSiteVisit;
  }

  private _createVisitDatesGroup() {
    return this.fb.group({
      startDate: new FormControl<Date | null>(null, [
        GovukValidators.required('Enter a date when the site visit began'),
        todayOrPastDateValidator(),
      ]),

      numberOfDays: new FormControl<number | null>(null, [
        GovukValidators.required('Enter the number of days your team were on site'),
        GovukValidators.naturalNumber('Must be integer greater than 0'),
      ]),
    });
  }

  addVisitDatesCtrl() {
    this.visitDatesCtrl.push(this._createVisitDatesGroup());
  }

  removeVisitDatesCtrl(index: number) {
    if (this.visitDatesCtrl.length > 1) {
      this.visitDatesCtrl.removeAt(index);
    }
  }

  private _visitDatesFormatted(visitDates: { startDate: string; numberOfDays: number }[]) {
    return visitDates.map((item) => {
      return {
        startDate: new Date(item.startDate),
        numberOfDays: item.numberOfDays,
      };
    });
  }

  private buildForm() {
    const formGroup = this.fb.group(
      {
        visitDates: this.fb.array(
          this.parentFormProvider.form.value.inPersonSiteVisitGroup?.visitDates.length
            ? this.parentFormProvider.form.value.inPersonSiteVisitGroup?.visitDates.map(() =>
                this._createVisitDatesGroup(),
              )
            : [this._createVisitDatesGroup()],
          {
            validators: GovukValidators.required(''),
          },
        ),
        teamMembers: new FormControl<string | null>(null, [
          GovukValidators.required('State which team members made the site visit'),
        ]),
      },
      { updateOn: 'change' },
    );

    formGroup.patchValue({
      teamMembers: this.parentFormProvider.form.value.inPersonSiteVisitGroup?.teamMembers ?? null,
      visitDates: this.parentFormProvider.form.value.inPersonSiteVisitGroup?.['visitDates']
        ? this._visitDatesFormatted(this.parentFormProvider.form.value.inPersonSiteVisitGroup?.['visitDates'])
        : [{ startDate: null, numberOfDays: null }],
    });

    return (this._form = formGroup);
  }
}
