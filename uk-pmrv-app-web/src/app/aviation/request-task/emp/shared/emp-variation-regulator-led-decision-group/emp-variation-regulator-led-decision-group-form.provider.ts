import { Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';

import { Subject } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { EmpAcceptedVariationDecisionDetails } from 'pmrv-api';

@Injectable()
export class EmpVariationRegulatorLedDecisionGroupFormProvider {
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder) {}

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  setFormValue(reviewDecision: EmpAcceptedVariationDecisionDetails) {
    if (reviewDecision?.variationScheduleItems?.length > 0) {
      this.form.setControl(
        'variationScheduleItems',
        this.fb.array(reviewDecision?.variationScheduleItems.map((ad) => this.createVariationScheduleItemsGroup(ad))),
      );
    }

    this.form.setValue({
      notes: reviewDecision?.notes ?? null,
      ...(reviewDecision?.variationScheduleItems?.length > 0
        ? {
            variationScheduleItems: reviewDecision.variationScheduleItems.map((ad) => ({ item: ad })),
          }
        : {}),
    });
  }

  addOtherVariationScheduleItem() {
    this.variationScheduleItemsCtrl.push(this.createVariationScheduleItemsGroup());
  }

  removeVariationScheduleItemsCtrl(index: number) {
    if (this.variationScheduleItemsCtrl.length > 1) {
      this.variationScheduleItemsCtrl.removeAt(index);
    } else {
      this.form.removeControl('variationScheduleItems');
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
        notes: [null as string, [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')]],
      },
      { updateOn: 'change' },
    );
  }

  public addVariationScheduleItemsCtrl(variationScheduleItem?: any) {
    this.form.addControl(
      'variationScheduleItems',
      this.fb.array([this.createVariationScheduleItemsGroup(variationScheduleItem)]),
    );
  }

  private createVariationScheduleItemsGroup(variationScheduleItem?: string): FormGroup {
    return this.fb.group({
      item: [
        variationScheduleItem ?? null,
        [
          GovukValidators.required('Add an item to the variation schedule'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  }

  private get variationScheduleItemsCtrl(): FormArray {
    return this.form.get('variationScheduleItems') as FormArray;
  }
}
