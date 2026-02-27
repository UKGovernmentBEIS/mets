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
import { BdrS2Service } from '@tasks/bdrs2/core';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRS2Bdrs2DataRegulatorReviewDecision,
} from 'pmrv-api';

import {
  BDRS2_REVIEW_GROUP_DECISION_FORM,
  bdrs2ReviewGroupDecisionFormProvider,
} from './bdrs2-review-group-decision-form.provider';
import { createAnotherRequiredChange } from './bdrs2-review-group-decision-form.util';

@Component({
  selector: 'app-bdrs2-review-group-decision',
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './bdrs2-review-group-decision.component.html',
  providers: [bdrs2ReviewGroupDecisionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2ReviewGroupDecisionComponent implements OnInit, PendingRequest {
  @Output() readonly notification = new EventEmitter<boolean>();
  @ViewChild('conditionalHeader') header: ElementRef;

  bdrs2Payload = this.bdrs2Service.payload as Signal<BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload>;

  decisionData$ = combineLatest([this.route.data, this.bdrs2Service.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        (payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewGroupDecisions?.[
          data?.groupKey
        ] as BDRS2Bdrs2DataRegulatorReviewDecision,
    ),
  );
  canEdit$ = combineLatest([this.route.data, this.bdrs2Service.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        !(payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          data?.groupKey
        ],
    ),
    tap((canEdit) => (canEdit ? (this.isOnEditState = true) : (this.isOnEditState = false))),
  );

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  uploadedFiles = [];
  isEditable$ = this.bdrs2Service.isEditable$;
  isOnEditState = false;

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  constructor(
    @Inject(BDRS2_REVIEW_GROUP_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly requestTaskFileService: RequestTaskFileService,
    readonly store: CommonTasksStore,
    readonly bdrs2Service: BdrS2Service,
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
            this.bdrs2Service.postGroupDecisionReview(
              this.constructReviewDecision(),
              'BDRS2_DATA',
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
        ...(this.bdrs2Payload()?.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam &&
        this.form.controls.decision.value === 'OPERATOR_AMENDS_NEEDED'
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
    return this.bdrs2Service.getBaseFileDownloadUrl();
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
