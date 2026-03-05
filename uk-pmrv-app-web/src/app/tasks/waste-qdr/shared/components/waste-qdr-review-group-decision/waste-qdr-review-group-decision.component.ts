import {
  ChangeDetectionStrategy,
  Component,
  computed,
  ElementRef,
  EventEmitter,
  Inject,
  OnInit,
  Output,
  Signal,
  ViewChild,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, startWith, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { ChangesRequestedTemplateComponent } from '@shared/components/changes-requested-template/changes-requested-template.component';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { WasteQdrService } from '@tasks/waste-qdr/core';

import { WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload, WasteQDRReviewDecision } from 'pmrv-api';

import {
  WASTE_QDR_REVIEW_GROUP_DECISION_FORM,
  wasteQDRReviewGroupDecisionFormProvider,
} from './waste-qdr-review-group-decision-form.provider';
import { createAnotherRequiredChange } from './waste-qdr-review-group-decision-form.utils';

interface ViewModel {
  isEditable: boolean;
  canEdit: boolean;
  decisionData: WasteQDRReviewDecision | undefined;
  downloadUrl: string;
  reviewAttachments: { [key: string]: string };
}
@Component({
  selector: 'app-waste-qdr-review-group-decision',
  templateUrl: './waste-qdr-review-group-decision.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, TaskSharedModule, ChangesRequestedTemplateComponent],
  providers: [wasteQDRReviewGroupDecisionFormProvider],
})
export class WasteQdrReviewGroupDecisionComponent implements OnInit, PendingRequest {
  @Output() readonly notification = new EventEmitter<boolean>();
  @ViewChild('conditionalHeader') header: ElementRef;

  wasteQDRPayload = this.wasteQDRService.payload as Signal<WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  payload = this.wasteQDRPayload;

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  uploadedFiles = [];
  isEditable = this.wasteQDRService.isEditable;
  isOnEditState = false;

  canEdit$ = combineLatest([this.route.data, this.wasteQDRService.getPayload()]).pipe(
    map(
      ([data, payload]) =>
        !(payload as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewSectionsCompleted?.[
          data?.groupKey
        ],
    ),
    tap((canEdit) => (canEdit ? (this.isOnEditState = true) : (this.isOnEditState = false))),
  );
  canEdit = toSignal(this.canEdit$);

  get requiredChanges(): FormArray {
    return this.form.get('requiredChanges') as FormArray;
  }

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const isEditable = this.isEditable();
    const canEdit = this.canEdit();
    const decisionData = payload.reviewDecision;
    const reviewAttachments = payload.regulatorReviewAttachments;

    return { isEditable, canEdit, decisionData, downloadUrl: this.getDownloadUrl(), reviewAttachments };
  });

  constructor(
    @Inject(WASTE_QDR_REVIEW_GROUP_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly requestTaskFileService: RequestTaskFileService,
    readonly store: CommonTasksStore,
    readonly wasteQDRService: WasteQdrService,
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
          switchMap(() =>
            this.wasteQDRService.postDecisionReview(
              this.constructReviewDecision(),
              'qdr',
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
    return this.wasteQDRService.getBaseFileDownloadUrl();
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
