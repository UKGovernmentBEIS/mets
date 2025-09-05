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
import { HseTiService } from '@tasks/hseti/core';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload, HSETIRegulatorReviewDecision } from 'pmrv-api';

import {
  HSETI_REVIEW_GROUP_DECISION_FORM,
  hsetiReviewGroupDecisionFormProvider,
} from './hseti-review-group-decision-form.provider';
import { createAnotherRequiredChange } from './hseti-review-group-decision-form.utils';

@Component({
  selector: 'app-hseti-review-group-decision',
  templateUrl: './hseti-review-group-decision.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, TaskSharedModule],
  providers: [hsetiReviewGroupDecisionFormProvider],
})
export class HsetiReviewGroupDecisionComponent implements OnInit, PendingRequest {
  @Output() readonly notification = new EventEmitter<boolean>();
  @ViewChild('conditionalHeader') header: ElementRef;

  hsetiPayload = this.hsetiService.payload as Signal<HSETIApplicationRegulatorReviewSubmitRequestTaskPayload>;

  decisionData$ = combineLatest([this.route.data, this.hsetiService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        (payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewGroupDecisions?.[
          data?.groupKey
        ] as HSETIRegulatorReviewDecision,
    ),
  );
  canEdit$ = combineLatest([this.route.data, this.hsetiService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        !(payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          data?.groupKey
        ],
    ),
    tap((canEdit) => (canEdit ? (this.isOnEditState = true) : (this.isOnEditState = false))),
  );

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  uploadedFiles = [];
  isEditable$ = this.hsetiService.isEditable$;
  isOnEditState = false;
  isEditable: Signal<boolean> = this.hsetiService.isEditable;

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  constructor(
    @Inject(HSETI_REVIEW_GROUP_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly requestTaskFileService: RequestTaskFileService,
    readonly store: CommonTasksStore,
    readonly hsetiService: HseTiService,
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
            this.hsetiService.postGroupDecisionReview(
              this.constructReviewDecision(),
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
        capacityIncreaseDescription: this.form.controls.capacityIncreaseDescription.value,
        capacityIncreasePermanence: this.form.controls.capacityIncreasePermanence.value,
        capacityGreaterThanZeroDescription: this.form.controls.capacityGreaterThanZeroDescription.value,
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
    return this.hsetiService.getBaseFileDownloadUrl();
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
