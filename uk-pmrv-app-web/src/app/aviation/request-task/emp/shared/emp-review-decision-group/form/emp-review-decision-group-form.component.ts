import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormArray } from '@angular/forms';

import { map, startWith } from 'rxjs';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskStore } from '../../../../store';
import { EmpReviewDecisionGroupFormProvider } from '../emp-review-decision-group-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-emp-review-decision-group-form',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './emp-review-decision-group-form.component.html',
  viewProviders: [existingControlContainer],
})
export class EmpReviewDecisionGroupFormComponent implements OnInit {
  @ViewChild('conditionalHeader') header: ElementRef;

  form = this.formProvider.form;
  uploadedFiles = [];

  constructor(private store: RequestTaskStore, readonly formProvider: EmpReviewDecisionGroupFormProvider) {}

  ngOnInit(): void {
    this.updateUploadedFiles();
  }

  addRequiredChangeCtrl() {
    this.formProvider.addRequiredChange();
    this.updateUploadedFiles();
  }

  removeRequiredChangeCtrl(index: number) {
    this.formProvider.removeRequiredChangeCtrl(index);
  }

  getDownloadUrl() {
    return `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/`;
  }

  get requiredChangeCtrl(): FormArray | null {
    return (this.form.get('requiredChanges') as FormArray) ?? null;
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
