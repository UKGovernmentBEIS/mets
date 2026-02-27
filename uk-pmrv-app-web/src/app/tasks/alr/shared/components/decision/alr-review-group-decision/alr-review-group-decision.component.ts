import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Inject,
  OnInit,
  Output,
  Signal,
  ViewChild,
} from '@angular/core';
import { FormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, startWith, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRAlrDataRegulatorReviewDecision, ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import {
  ALR_REVIEW_GROUP_DECISION_FORM,
  alrReviewGroupDecisionFormProvider,
} from './alr-review-group-decision-form.provider';
import { createAnotherRequiredChange } from './alr-review-group-decision-form.utils';

@Component({
  selector: 'app-alr-review-group-decision',
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './alr-review-group-decision.component.html',
  providers: [alrReviewGroupDecisionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrReviewGroupDecisionComponent implements OnInit, PendingRequest {
  @Output() readonly notification = new EventEmitter<boolean>();
  @ViewChild('conditionalHeader') header: ElementRef;

  alrPayload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  decisionData$ = combineLatest([this.route.data, this.alrService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        (payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewGroupDecisions?.[
          data?.groupKey
        ] as ALRAlrDataRegulatorReviewDecision,
    ),
  );
  canEdit$ = combineLatest([this.route.data, this.alrService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        !(payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          data?.groupKey
        ],
    ),
    tap((canEdit) => (canEdit ? (this.isOnEditState = true) : (this.isOnEditState = false))),
  );

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  uploadedFiles = [];
  isEditable$ = this.alrService.isEditable$;
  isOnEditState = false;

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  constructor(
    @Inject(ALR_REVIEW_GROUP_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly requestTaskFileService: RequestTaskFileService,
    readonly store: CommonTasksStore,
    readonly alrService: AlrService,
    readonly router: Router,
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
            this.alrService.postGroupDecisionReview(
              this.constructReviewDecision(),
              'ALR_DATA',
              data.groupKey,
              this.form.controls.requiredChanges.value.map((requiredChange) => requiredChange.files).flat(),
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.router.navigate(['../'], { relativeTo: this.route });
        });
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }

  private constructReviewDecision(): any {
    return {
      type: this.form.controls.decision.value,
      details: {
        ...(this.form.controls.decision.value === 'OPERATOR_AMENDS_NEEDED'
          ? { verificationRequired: this.form.controls.verificationRequired.value }
          : {}),
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
    return this.alrService.getBaseFileDownloadUrl();
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
