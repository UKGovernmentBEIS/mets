import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, Observable, startWith, takeUntil, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpReviewDecision,
} from 'pmrv-api';

import { RequestTaskFileService } from '../../../../../shared/services/request-task-file-service/request-task-file.service';
import { PermitNotificationService } from '../../../core/permit-notification.service';
import {
  createAnotherRequiredChange,
  FOLLOW_UP_REVIEW_DECISION_FORM,
  followUpReviewDecisionFormProvider,
} from './decision-form.provider';

@Component({
  selector: 'app-follow-up-decision',
  templateUrl: './decision.component.html',
  providers: [followUpReviewDecisionFormProvider, DestroySubject, BreadcrumbService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpReviewDecisionComponent implements OnInit {
  get requiredChanges(): UntypedFormArray {
    return this.form.get('requiredChanges') as UntypedFormArray;
  }

  isFileUploaded = this.form.get('requiredChanges')['controls'].map((requiredChange) =>
    requiredChange.get('files').valueChanges.pipe(
      startWith(requiredChange.get('files').value),
      map((value: []) => value?.length > 0),
    ),
  );

  isEditable$ = this.store.select('isEditable');
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  showSummary$ = new BehaviorSubject<boolean>(false);
  files: { downloadUrl: string; fileName: string }[];
  followUpFiles$ = new BehaviorSubject<{ downloadUrl: string; fileName: string }[]>([]);
  showNotificationBanner = false;

  reviewPayload$: Observable<PermitNotificationFollowUpApplicationReviewRequestTaskPayload> =
    this.permitNotificationService.getPayload().pipe(
      first(),
      tap((taskPayload: PermitNotificationFollowUpApplicationReviewRequestTaskPayload) => {
        if (taskPayload.reviewDecision?.type && taskPayload.reviewSectionsCompleted.RESPONSE !== false) {
          this.showSummary$.next(true);
        }

        if (taskPayload.followUpFiles) {
          this.files = this.permitNotificationService.getDownloadUrlFiles(taskPayload.followUpFiles);
        }
      }),
    );

  summaryData$ = this.reviewPayload$.pipe(
    map((p: PermitNotificationFollowUpApplicationReviewRequestTaskPayload) => p.reviewDecision as any),
  );

  constructor(
    @Inject(FOLLOW_UP_REVIEW_DECISION_FORM) readonly form: UntypedFormGroup,
    public readonly route: ActivatedRoute,
    public readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    public readonly permitNotificationService: PermitNotificationService,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
    readonly requestTaskFileService: RequestTaskFileService,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((paramMap) => {
      const link = ['/tasks', paramMap.get('taskId'), 'permit-notification', 'follow-up', 'review'];
      this.breadcrumbService.show([{ text: 'Permit Notification Follow Up Review', link }]);
    });

    this.enableOptionalFields();
  }

  getDownloadUrl() {
    return this.permitNotificationService.createBaseFileDownloadUrl();
  }

  onSubmit(): void {
    if (this.form.valid) {
      const type: PermitNotificationFollowUpReviewDecision['type'] = this.form.get('type').value;
      const payload: PermitNotificationFollowUpReviewDecision = {
        type,
        details: {
          notes: this.form.controls.notes.value,
          ...(type === 'AMENDS_NEEDED'
            ? {
                requiredChanges: this.form.controls.requiredChanges.value.map((requiredChange) => ({
                  reason: requiredChange.reason,
                  files: requiredChange.files.map((file) => file.uuid),
                })),
                dueDate: this.form.get('dueDate').value,
              }
            : null),
        },
      };

      this.permitNotificationService
        .postFollowUpDecision(payload)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          let newFollowUpAttachments;

          this.form.controls.requiredChanges.value
            .map((requiredChange) => requiredChange.files)
            .flat()
            .forEach((e: { uuid: string; file: { name: string } }) => {
              newFollowUpAttachments = {
                ...newFollowUpAttachments,
                [e.uuid]: e.file.name,
              };
            });

          this.store.setState({
            ...this.store.getState(),
            requestTaskItem: {
              ...this.store.getState().requestTaskItem,
              requestTask: {
                ...this.store.getState().requestTaskItem.requestTask,
                payload: {
                  ...this.store.getState().requestTaskItem.requestTask.payload,
                  reviewSectionsCompleted: {},
                  followUpAttachments: {
                    ...this.store.getState().requestTaskItem.requestTask.payload['followUpAttachments'],
                    ...newFollowUpAttachments,
                  },
                } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
              },
            },
          });

          this.showSummary$.next(true);
          this.showNotificationBanner = true;
        });
    } else {
      this.isErrorSummaryDisplayed$.next(true);
      this.showNotificationBanner = false;
    }
  }

  changeDecisionClick() {
    this.showSummary$.next(false);
  }

  addOtherRequiredChange(): void {
    this.requiredChanges.push(createAnotherRequiredChange(this.store, this.requestTaskFileService, null));
  }

  private enableOptionalFields() {
    this.form
      .get('type')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((type: PermitNotificationFollowUpReviewDecision['type']) => {
        if (type === 'AMENDS_NEEDED') {
          this.form.get('requiredChanges').enable();
          this.form.get('dueDate').enable();
        }
      });
  }
}
