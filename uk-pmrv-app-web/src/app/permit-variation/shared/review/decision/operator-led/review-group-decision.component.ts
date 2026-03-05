import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Inject,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { FormArray, UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject, map, Observable, startWith } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { createAnotherRequiredChange } from '@permit-application/shared/decision/review-group-decision-form-utils';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { resolveReviewGroupStatus } from '../../../../../permit-application/review/utils/review';
import { RequestTaskFileService } from '../../../../../shared/services/request-task-file-service/request-task-file.service';
import { PermitVariationStore } from '../../../../store/permit-variation.store';
import { AboutVariationGroupKey } from '../../../../variation-types';
import { createAnotherVariationScheduleItem } from '../review-group-decision-form-utils';
import { REVIEW_GROUP_DECISION_FORM, reviewGroupDecisionFormProvider } from './review-group-decision-form.provider';

@Component({
  selector: 'app-variation-operator-led-review-group-decision',
  templateUrl: './review-group-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, reviewGroupDecisionFormProvider],
})
export class ReviewGroupDecisionComponent implements OnInit, PendingRequest {
  @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'] | AboutVariationGroupKey;
  @Input() canEdit = true;
  @Output() readonly notification = new EventEmitter<boolean>();
  @ViewChild('conditionalHeader') header: ElementRef;

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  get variationScheduleItems(): FormArray {
    return this.form.get('variationScheduleItems') as FormArray;
  }

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  uploadedFiles = [];

  isEditable$ = this.store.pipe(map((state) => state.isEditable));
  isOnEditState = false;
  isOnEditByDefaultState$: Observable<boolean>;

  constructor(
    @Inject(REVIEW_GROUP_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitVariationStore,
    readonly requestTaskFileService: RequestTaskFileService,
  ) {}

  ngOnInit(): void {
    this.isOnEditByDefaultState$ = this.store.pipe(
      map((state) => {
        if (this.groupKey === 'ABOUT_VARIATION') {
          return !state.permitVariationDetailsReviewCompleted;
        } else {
          return ['undecided', 'needs review'].includes(resolveReviewGroupStatus(state, this.groupKey));
        }
      }),
    );

    this.updateUploadedFiles();
  }

  onSubmit(): void {
    if (this.form.valid) {
      const decision = this.constructReviewDecision();
      const files = this.form.controls.requiredChanges.value.map((requiredChange) => requiredChange.files).flat();
      if (this.groupKey === 'ABOUT_VARIATION') {
        this.store
          .postVariationDetailsReviewDecision(decision, files)
          .pipe(this.pendingRequest.trackRequest())
          .subscribe(() => {
            this.isOnEditState = false;
            this.notification.emit(true);
          });
      } else {
        this.store
          .postReview(decision, this.groupKey, files)
          .pipe(this.pendingRequest.trackRequest())
          .subscribe(() => {
            this.isOnEditState = false;
            this.notification.emit(true);
          });
      }
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }

  private constructReviewDecision(): any {
    return {
      type: this.form.controls.decision.value,
      details: {
        notes: this.form.controls.notes.value,
        ...(this.form.controls.decision.value === 'OPERATOR_AMENDS_NEEDED'
          ? {
              requiredChanges: this.form.controls.requiredChanges.value.map((requiredChange) => ({
                reason: requiredChange.reason,
                files: requiredChange.files.map((file) => file.uuid),
              })),
            }
          : {}),
        ...(this.form.controls.decision.value === 'ACCEPTED' && this.form.controls.variationScheduleItems.value.length
          ? {
              variationScheduleItems: this.form.controls.variationScheduleItems.value.map(
                (variationScheduleItem) => variationScheduleItem.item,
              ),
            }
          : {}),
      },
    };
  }

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }

  addOtherRequiredChange(): void {
    this.requiredChanges.push(createAnotherRequiredChange(this.store, this.requestTaskFileService, null));

    this.updateUploadedFiles();
  }

  updateUploadedFiles() {
    this.uploadedFiles = this.form.get('requiredChanges')['controls'].map((requiredChange) =>
      requiredChange.get('files').valueChanges.pipe(
        startWith(requiredChange.get('files').value),
        map((value: []) => value?.length > 0),
      ),
    );
  }

  addOtherVariationScheduleItem(): void {
    this.variationScheduleItems.push(createAnotherVariationScheduleItem(null));
  }
}
