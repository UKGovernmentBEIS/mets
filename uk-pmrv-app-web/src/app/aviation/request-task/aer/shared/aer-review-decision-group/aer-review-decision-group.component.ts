import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, filter, map, Observable, takeUntil } from 'rxjs';

import {
  AerCorsiaReviewGroup,
  aerCorsiaReviewGroupMap,
} from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { CorsiaRequestTypes } from '@aviation/request-task/util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { AerReviewCorsiaTaskKey, AerTaskKey, requestTaskQuery, RequestTaskStore } from '../../../store';
import { aerQuery } from '../aer.selectors';
import { aerReviewGroupMap, AerUkEtsReviewGroup } from '../util/aer.util';
import { AerReviewDecisionGroupFormProvider } from './aer-review-decision-group-form.provider';
import { AerReviewDecisionGroupFormComponent } from './form/aer-review-decision-group-form.component';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-aer-review-decision-group',
  standalone: true,
  imports: [SharedModule, AerReviewDecisionGroupFormComponent, AerReviewDecisionGroupSummaryComponent],
  templateUrl: './aer-review-decision-group.component.html',
  providers: [DestroySubject],
})
export class AerReviewDecisionGroupComponent implements OnInit, OnDestroy {
  @Input() taskKey: AerTaskKey | AerReviewCorsiaTaskKey;
  @Input() addValidator: ValidatorFn;

  allowedReviewEditableActions = [
    'AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION',
    'AVIATION_AER_CORSIA_SAVE_REVIEW_GROUP_DECISION',
  ];

  form = this.formProvider.form;
  isEditable$ = this.store.pipe(
    requestTaskQuery.selectRelatedActions,
    map((actions) => actions?.some((el) => this.allowedReviewEditableActions.includes(el))),
  );
  isOnEditState = false;
  canEdit$: Observable<boolean>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  decisionData$: Observable<any>;
  attachments$: Observable<{ [key: string]: string }> = this.store.pipe(aerQuery.selectReviewAttachments);

  private internalSet = false;
  private groupKey: AerUkEtsReviewGroup | AerCorsiaReviewGroup;

  constructor(
    public store: RequestTaskStore,
    private destroy$: DestroySubject,
    private pendingRequestService: PendingRequestService,
    private route: ActivatedRoute,
    private router: Router,
    readonly formProvider: AerReviewDecisionGroupFormProvider,
  ) {}

  ngOnInit(): void {
    this.groupKey = CorsiaRequestTypes.includes(this.store.getState().requestTaskItem.requestInfo.type)
      ? aerCorsiaReviewGroupMap[this.taskKey]
      : aerReviewGroupMap[this.taskKey];

    this.canEdit$ = this.store.pipe(
      aerQuery.selectReviewSectionsCompleted,
      map((reviewSectionsCompleted) => !reviewSectionsCompleted[this.groupKey]),
    );

    this.decisionData$ = this.store.pipe(
      aerQuery.selectReviewDecisions,
      map((reviewDecisions) => reviewDecisions[this.groupKey]),
    );

    this.store
      .pipe(
        aerQuery.selectReviewDecisions,
        filter((reviewDecisions) => !!reviewDecisions[this.groupKey]),
        takeUntil(this.destroy$),
      )
      .subscribe((reviewDecisions) => {
        if (!this.internalSet) {
          this.formProvider.setFormValue(reviewDecisions[this.groupKey]);
        }
      });

    if (this.addValidator) {
      this.form.addValidators(this.addValidator);
    }
  }

  ngOnDestroy(): void {
    this.formProvider.destroyForm();
  }

  onSubmit() {
    if (this.form.valid) {
      this.internalSet = true;
      (this.store.aerDelegate as AerUkEtsStoreDelegate)
        .saveAerReviewDecision(this.getFormData(), this.taskKey as any)
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe({
          next: () => {
            this.form.value?.requiredChanges
              ?.map((requiredChanges) => requiredChanges?.files)
              ?.forEach((files) =>
                files.forEach((file) => this.store.aerDelegate.addReviewAttachment({ [file.uuid]: file.file.name })),
              );

            this.internalSet = false;
            this.isOnEditState = false;

            const isCorsia = CorsiaRequestTypes.includes(this.store.getState().requestTaskItem.requestInfo.type);
            const path = isCorsia
              ? '../..'
              : this.router.url.split('/').at(-1) === 'summary'
                ? '../../../..'
                : '../../..';

            this.router.navigate([path], { relativeTo: this.route });
          },
          complete: () => (this.internalSet = false),
        });
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }

  private getFormData(): any {
    return {
      type: this.form.value.type,
      details: {
        notes: this.form.value.notes,
        ...(this.form.value.type === 'ACCEPTED'
          ? {}
          : {
              requiredChanges:
                this.form.value.requiredChanges?.map((changes) => ({
                  ...changes,
                  files: changes?.files?.map((doc: FileUpload) => doc.uuid),
                })) ?? [],
            }),
      },
    };
  }
}
