import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormArray } from '@angular/forms';

import { map, startWith } from 'rxjs';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskStore } from '../../../../store';
import { AerReviewDecisionGroupFormProvider } from '../aer-review-decision-group-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-aer-review-decision-group-form',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './aer-review-decision-group-form.component.html',
  viewProviders: [existingControlContainer],
})
export class AerReviewDecisionGroupFormComponent implements OnInit {
  @ViewChild('conditionalHeader') header: ElementRef;

  form = this.formProvider.form;
  uploadedFiles = [];

  constructor(private store: RequestTaskStore, readonly formProvider: AerReviewDecisionGroupFormProvider) {}

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
    return `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`;
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
