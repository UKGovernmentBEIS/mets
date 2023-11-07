import { Injectable } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import {
  EmpVariationReviewDecision,
  EmpVariationUkEtsApplicationReviewRequestTaskPayload,
  ReviewDecisionRequiredChange,
} from 'pmrv-api';

import { RequestTaskStore } from '../../../store';

@Injectable()
export class EmpVariationReviewDecisionGroupFormProvider {
  private _form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private requestTaskFileService: RequestTaskFileService,
    private store: RequestTaskStore,
  ) {}

  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  setFormValue(reviewDecision: EmpVariationReviewDecision) {
    this.form.setValue({
      type: reviewDecision?.type ?? null,
      notes: reviewDecision?.details?.notes ?? null,
    });

    if (
      reviewDecision?.type === 'OPERATOR_AMENDS_NEEDED' &&
      (reviewDecision as any)?.details?.requiredChanges?.length > 0
    ) {
      this.form.setControl(
        'requiredChanges',
        this.fb.array(
          (reviewDecision as any)?.details?.requiredChanges.map((ad) => this.createRequiredChangeGroup(ad)),
        ),
      );
    }

    if (reviewDecision?.type === 'ACCEPTED' && (reviewDecision as any)?.details?.variationScheduleItems?.length > 0) {
      this.form.setControl(
        'variationScheduleItems',
        this.fb.array(
          (reviewDecision as any)?.details?.variationScheduleItems.map((ad) =>
            this.createVariationScheduleItemsGroup(ad),
          ),
        ),
      );
    }
  }

  addRequiredChange() {
    this.requiredChangeCtrl.push(this.createRequiredChangeGroup());
  }

  addOtherVariationScheduleItem() {
    this.variationScheduleItemsCtrl.push(this.createVariationScheduleItemsGroup());
  }

  addVariationScheduleItemsCtrl(variationScheduleItem?: any) {
    this.form.addControl(
      'variationScheduleItems',
      this.fb.array([this.createVariationScheduleItemsGroup(variationScheduleItem)]),
    );
  }

  removeRequiredChangeCtrl(index: number) {
    if (this.requiredChangeCtrl.length > 1) {
      this.requiredChangeCtrl.removeAt(index);
    }
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
        type: [null as string, [GovukValidators.required('Select a decision for this review group')]],
        notes: [null as string, [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')]],
      },
      { updateOn: 'change' },
    );

    this.typeCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((decisionType) => {
      if (decisionType === 'OPERATOR_AMENDS_NEEDED') {
        this.addRequiredChangeCtrl();
        this.form.removeControl('variationScheduleItems');
      } else if (decisionType === 'ACCEPTED') {
        this.form.removeControl('requiredChanges');
      } else {
        this.form.removeControl('requiredChanges');
        this.form.removeControl('variationScheduleItems');
      }
    });
  }

  private addRequiredChangeCtrl(decisionRequiredChange?: ReviewDecisionRequiredChange) {
    this.form.addControl('requiredChanges', this.fb.array([this.createRequiredChangeGroup(decisionRequiredChange)]));
  }

  private createRequiredChangeGroup(decisionRequiredChange?: ReviewDecisionRequiredChange): FormGroup {
    return this.fb.group({
      reason: [
        decisionRequiredChange?.reason ?? null,
        [
          GovukValidators.required('Enter the change required by the operator'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      files: this.requestTaskFileService.buildFormControl(
        this.store.requestTaskId,
        decisionRequiredChange?.files ?? [],
        (
          this.store.getState().requestTaskItem?.requestTask
            ?.payload as EmpVariationUkEtsApplicationReviewRequestTaskPayload
        )?.reviewAttachments, //TODO consider corsia as well
        'EMP_VARIATION_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
        false,
        !this.store.getState().isEditable,
      ),
    });
  }

  private createVariationScheduleItemsGroup(variationScheduleItem?: string[]): FormGroup {
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

  private get typeCtrl(): AbstractControl {
    return this.form.get('type');
  }

  private get requiredChangeCtrl(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  private get variationScheduleItemsCtrl(): FormArray {
    return this.form.get('variationScheduleItems') as FormArray;
  }
}
