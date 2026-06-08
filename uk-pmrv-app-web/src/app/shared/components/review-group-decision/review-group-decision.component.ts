import {
  ChangeDetectionStrategy,
  Component,
  computed,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
  Signal,
  signal,
} from '@angular/core';
import { FormArray, FormControl, FormGroup, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { map, startWith } from 'rxjs';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { SharedModule } from '@shared/shared.module';
import { ReviewDecisionPayload } from '@shared/types';
import { AttachedFile } from '@shared/types/attached-file.type';

import { GovukValidators } from 'govuk-components';

import { ReviewDecisionRequiredChange } from 'pmrv-api';

import { createAnotherRequiredChange, getFileListTitle, taskHasNoVerification } from './review-group-decision.utils';

@Component({
  selector: 'app-shared-review-group-decision',
  imports: [SharedModule],
  templateUrl: './review-group-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewGroupDecisionSharedComponent implements OnInit {
  @Input() isEditable: boolean;
  @Input() payload: ReviewDecisionPayload;
  @Input() downloadUrl: string;
  @Input() requestTaskId: number;
  @Input() hideAmendsOption: boolean;
  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  private readonly route = inject(ActivatedRoute);
  private readonly requestTaskFileService = inject(RequestTaskFileService);

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  hasError = signal(false);
  isOnEditState = signal(false);
  hasNoVerification = signal(false);
  uploadedFiles: Array<any> = [];
  fileListTitle = signal('');
  form = new FormGroup({
    verificationRequired: new FormControl(null, {
      validators: [GovukValidators.required('Select yes if the operator needs to send the amends to the verifier')],
    }),
    decision: new FormControl(null, { validators: [GovukValidators.required('Select a decision')] }),
    notes: new FormControl(null, { validators: [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')] }),
    requiredChanges: new FormArray([]),
  });

  canEdit = computed(() => {
    const groupKey = this.route.snapshot.data.groupKey;
    const canEdit = !(this.payload.regulatorReviewSectionsCompleted?.[groupKey] ?? false);

    return canEdit;
  });

  decisionData: Signal<any> = computed(() => {
    const groupKey = this.route.snapshot.data.groupKey;

    return this.payload?.regulatorReviewGroupDecisions?.[groupKey] || (this.payload as any)?.reviewDecision;
  });

  ngOnInit(): void {
    const groupKey = this.route.snapshot.data.groupKey;
    const { regulatorReviewGroupDecisions, payloadType } = this.payload;
    const reviewDecision = (regulatorReviewGroupDecisions?.[groupKey] as any) || (this.payload as any)?.reviewDecision;

    this.fileListTitle.set(getFileListTitle(payloadType));
    this.hasNoVerification.set(taskHasNoVerification(payloadType));

    this.form.patchValue({
      verificationRequired: reviewDecision?.details?.verificationRequired ?? null,
      decision: reviewDecision?.type ?? null,
      notes: reviewDecision?.details?.notes ?? null,
    });

    if (this.isEditable) {
      if (this.hideAmendsOption) {
        this.form.controls['verificationRequired'].disable();
      } else {
        if (this.hasNoVerification()) {
          this.form.controls['verificationRequired'].disable();
        }

        const requiredChanges = (reviewDecision?.details as { requiredChanges: ReviewDecisionRequiredChange[] })
          ?.requiredChanges;

        (requiredChanges || [null])?.map((requiredChange) => this.addOtherRequiredChange(requiredChange));
      }
    } else {
      this.form.controls['verificationRequired'].disable();
      this.form.controls['decision'].disable();
      this.form.controls['notes'].disable();
    }

    this.changeEditState(this.canEdit());
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.formSubmit.emit(this.form);
    } else {
      this.hasError.set(true);
    }
  }

  addOtherRequiredChange(value?: any): void {
    this.requiredChanges.push(
      createAnotherRequiredChange(
        this.requestTaskId,
        this.payload.regulatorReviewAttachments,
        this.requestTaskFileService,
        this.payload.payloadType,
        value,
      ),
    );
    this.updateUploadedFiles();
  }

  updateUploadedFiles() {
    this.uploadedFiles = (this.form.get('requiredChanges') as FormArray)['controls'].map((requiredChange) =>
      requiredChange.get('files').valueChanges.pipe(
        startWith(requiredChange.get('files').value),
        map((value: []) => value?.length > 0),
      ),
    );
  }

  changeEditState(value = false) {
    this.isOnEditState.set(value);
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const url = this.downloadUrl;
    const regulatorReviewAttachments: { [key: string]: string } = this.payload?.regulatorReviewAttachments;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: regulatorReviewAttachments[id],
      })) ?? []
    );
  }
}
