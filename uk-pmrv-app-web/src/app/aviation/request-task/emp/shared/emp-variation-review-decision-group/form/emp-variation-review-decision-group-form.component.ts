import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormArray } from '@angular/forms';

import { map, Observable, startWith } from 'rxjs';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { requestTaskQuery, RequestTaskStore } from '../../../../store';
import { EmpVariationReviewDecisionGroupFormProvider } from '../emp-variation-review-decision-group.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-emp-variation-review-decision-group-form',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './emp-variation-review-decision-group-form.component.html',
  viewProviders: [existingControlContainer],
})
export class EmpVariationReviewDecisionGroupFormComponent implements OnInit {
  @ViewChild('conditionalHeader') header: ElementRef;

  form = this.formProvider.form;
  uploadedFiles = [];
  isEditable$: Observable<boolean>;

  constructor(readonly store: RequestTaskStore, readonly formProvider: EmpVariationReviewDecisionGroupFormProvider) {}

  ngOnInit(): void {
    this.updateUploadedFiles();
    this.isEditable$ = this.store.pipe(requestTaskQuery.selectIsEditable);
  }

  addRequiredChangeCtrl() {
    this.formProvider.addRequiredChange();
    this.updateUploadedFiles();
  }

  addOtherVariationScheduleItem(): void {
    this.formProvider.addOtherVariationScheduleItem();
  }

  addVariationScheduleItem(): void {
    this.formProvider.addVariationScheduleItemsCtrl();
  }

  removeRequiredChangeCtrl(index: number) {
    this.formProvider.removeRequiredChangeCtrl(index);
  }

  removeVariationScheduleItems(index: number) {
    this.formProvider.removeVariationScheduleItemsCtrl(index);
  }

  getDownloadUrl() {
    return `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/`;
  }

  get requiredChangeCtrl(): FormArray | null {
    return (this.form.get('requiredChanges') as FormArray) ?? null;
  }

  get variationScheduleItemsCtrl(): FormArray | null {
    return (this.form.get('variationScheduleItems') as FormArray) ?? null;
  }

  private updateUploadedFiles() {
    this.uploadedFiles = this.form.get('requiredChanges')
      ? this.form.get('requiredChanges')['controls'].map((requiredChange) =>
          requiredChange.get('files').valueChanges.pipe(
            startWith(requiredChange.get('files').value),
            map((value: []) => value?.length > 0),
          ),
        )
      : [];
  }
}
