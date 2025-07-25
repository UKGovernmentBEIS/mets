import { Injectable } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { EmpIssuanceReviewDecision, ReviewDecisionRequiredChange } from 'pmrv-api';

import {
  EmpIssuanceReviewRequestTaskPayload,
  uploadReviewGroupDecisionAttachmentActionTypeByRequestTaskTypeMap,
} from '../util/emp.util';

@Injectable()
export class EmpReviewDecisionGroupFormProvider {
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

  setFormValue(reviewDecision: EmpIssuanceReviewDecision) {
    this.form.patchValue({
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
  }

  addRequiredChange() {
    this.requiredChangeCtrl.push(this.createRequiredChangeGroup());
  }

  removeRequiredChangeCtrl(index: number) {
    if (this.requiredChangeCtrl.length > 1) {
      this.requiredChangeCtrl.removeAt(index);
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
      } else {
        this.form.removeControl('requiredChanges');
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
        (this.store.getState().requestTaskItem?.requestTask?.payload as EmpIssuanceReviewRequestTaskPayload)
          ?.reviewAttachments ?? {},
        uploadReviewGroupDecisionAttachmentActionTypeByRequestTaskTypeMap(
          this.store.getState().requestTaskItem?.requestTask?.type,
        ),
        false,
        !this.store.getState().isEditable,
      ),
    });
  }

  private get typeCtrl(): AbstractControl {
    return this.form.get('type');
  }

  private get requiredChangeCtrl(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }
}
