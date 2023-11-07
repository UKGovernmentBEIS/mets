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
import { resolveReviewGroupStatus } from '@permit-application/review/utils/review';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { REVIEW_GROUP_DECISION_FORM, reviewGroupDecisionFormProvider } from './review-group-decision-form.provider';
import { createAnotherRequiredChange } from './review-group-decision-form-utils';

@Component({
  selector: 'app-review-group-decision',
  templateUrl: './review-group-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, reviewGroupDecisionFormProvider],
})
export class ReviewGroupDecisionComponent implements OnInit, PendingRequest {
  @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
  @Input() canEdit = true;
  @Input() reviewGroupStatus$ = this.store.pipe(map((state) => resolveReviewGroupStatus(state, this.groupKey)));
  @Input() reviewGroupDecision: { [key: string]: any };
  @Output() readonly notification = new EventEmitter<boolean>();
  @ViewChild('conditionalHeader') header: ElementRef;

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  uploadedFiles = [];

  isEditable$ = this.store.pipe(map((state) => state.isEditable));
  isOnEditState = false;
  isOnEditByDefaultState$: Observable<boolean>;
  hideAmends: boolean;

  constructor(
    @Inject(REVIEW_GROUP_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly requestTaskFileService: RequestTaskFileService,
  ) {}

  ngOnInit(): void {
    this.hideAmends = this.store.amendsIsNotNeeded(this.groupKey);

    this.isOnEditByDefaultState$ = this.reviewGroupStatus$.pipe(
      map((reviewGroupStatus) => ['undecided', 'needs review'].includes(reviewGroupStatus)),
    );

    this.updateUploadedFiles();
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.store
        .postReview(
          this.constructReviewDecision(),
          this.groupKey,
          this.form.controls.requiredChanges.value.map((requiredChange) => requiredChange.files).flat(),
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.isOnEditState = false;
          this.notification.emit(true);
        });
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
}
