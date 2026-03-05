import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Inject,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { FormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AerService } from '@tasks/aer/core/aer.service';
import {
  AER_REVIEW_GROUP_DECISION_FORM,
  aerReviewGroupDecisionFormProvider,
} from '@tasks/aer/shared/components/decision/aer-review-group-decision/aer-review-group-decision-form.provider';
import { createAnotherRequiredChange } from '@tasks/aer/shared/components/decision/aer-review-group-decision/aer-review-group-decision-form-utils';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationReviewRequestTaskPayload, AerDataReviewDecision } from 'pmrv-api';

@Component({
  selector: 'app-aer-review-group-decision',
  templateUrl: './aer-review-group-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [aerReviewGroupDecisionFormProvider],
})
export class AerReviewGroupDecisionComponent implements OnInit, PendingRequest {
  @Output() readonly notification = new EventEmitter<boolean>();
  @ViewChild('conditionalHeader') header: ElementRef;

  decisionData$ = combineLatest([this.route.data, this.aerService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        (payload as AerApplicationReviewRequestTaskPayload)?.reviewGroupDecisions[
          data.groupKey
        ] as AerDataReviewDecision,
    ),
  );
  canEdit$ = combineLatest([this.route.data, this.aerService.getPayload()]).pipe(
    map(
      ([data, payload]) => !(payload as AerApplicationReviewRequestTaskPayload)?.reviewSectionsCompleted[data.groupKey],
    ),
  );

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  uploadedFiles = [];
  isEditable$ = this.aerService.isEditable$;
  isOnEditState = false;

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  constructor(
    @Inject(AER_REVIEW_GROUP_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly requestTaskFileService: RequestTaskFileService,
    readonly store: CommonTasksStore,
    readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.updateUploadedFiles();
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.aerService.postGroupDecisionReview(
              this.constructReviewDecision(),
              'AER_DATA',
              data.groupKey,
              this.form.controls.requiredChanges.value.map((requiredChange) => requiredChange.files).flat(),
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
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
    return this.aerService.getBaseFileDownloadUrl();
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
