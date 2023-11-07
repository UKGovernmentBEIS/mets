import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ValidatorFn } from '@angular/forms';

import { BehaviorSubject, filter, map, Observable, takeUntil } from 'rxjs';

import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { EmpVariationReviewDecision } from 'pmrv-api';

import { EmpTaskKey, requestTaskQuery, RequestTaskStore } from '../../../store';
import { empQuery } from '../../shared/emp.selectors';
import { EmpReviewGroup, empReviewGroupMap } from '../../shared/util/emp.util';
import { EmpVariationReviewDecisionGroupFormProvider } from './emp-variation-review-decision-group.provider';
import { EmpVariationReviewDecisionGroupFormComponent } from './form/emp-variation-review-decision-group-form.component';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-emp-variation-review-decision-group',
  standalone: true,
  imports: [
    SharedModule,
    EmpVariationReviewDecisionGroupFormComponent,
    EmpVariationReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './emp-variation-review-decision-group.component.html',
  providers: [DestroySubject],
})
export class EmpVariationReviewDecisionGroupComponent implements OnInit, OnDestroy {
  @Input() taskKey: EmpTaskKey;
  @Input() addValidator: ValidatorFn;

  form = this.formProvider.form;
  isEditable$ = this.store.pipe(requestTaskQuery.selectIsEditable);
  isOnEditState = false;
  canEdit$: Observable<boolean>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  decisionData$: Observable<EmpVariationReviewDecision>;
  attachments$: Observable<{ [key: string]: string }> = this.store.pipe(empQuery.selectReviewAttachments);

  private internalSet = false;
  private groupKey: EmpReviewGroup; //TODO consider corsia as well

  constructor(
    public store: RequestTaskStore,
    private destroy$: DestroySubject,
    private pendingRequestService: PendingRequestService,
    readonly formProvider: EmpVariationReviewDecisionGroupFormProvider,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  ngOnInit(): void {
    this.groupKey = empReviewGroupMap[this.taskKey];

    if (this.taskKey === 'empVariationDetails') {
      this.decisionData$ = this.store.pipe(empQuery.selectVariationDetailsReviewDecisions);
      this.canEdit$ = this.store.pipe(
        empQuery.selectVariationDetailsReviewCompleted,
        map((reviewSectionsCompleted) => !reviewSectionsCompleted),
      );

      this.store
        .pipe(
          empQuery.selectVariationDetailsReviewDecisions,
          filter((reviewDecision) => !!reviewDecision),
          takeUntil(this.destroy$),
        )
        .subscribe((reviewDecision) => {
          if (!this.internalSet) {
            this.formProvider.setFormValue(reviewDecision);
          }
        });
    } else {
      this.decisionData$ = this.store.pipe(
        empQuery.selectVariationReviewDecisions,
        map((reviewDecisions) => reviewDecisions[this.groupKey]),
      );
      this.canEdit$ = this.store.pipe(
        empQuery.selectReviewSectionsCompleted,
        map((reviewSectionsCompleted) => !reviewSectionsCompleted[this.groupKey]),
      );

      this.store
        .pipe(
          empQuery.selectVariationReviewDecisions,
          filter((reviewDecisions) => !!reviewDecisions[this.groupKey]),
          takeUntil(this.destroy$),
        )
        .subscribe((reviewDecisions) => {
          if (!this.internalSet) {
            this.formProvider.setFormValue(reviewDecisions[this.groupKey]);
          }
        });
    }

    if (this.addValidator) {
      this.form.addValidators(this.addValidator);
    }
  }

  ngOnDestroy(): void {
    this.formProvider.destroyForm();
  }

  onSubmit() {
    //TODO consider corsia as well

    if (this.form.valid) {
      this.internalSet = true;
      this.store.empDelegate
        .saveEmpVariationReviewDecision(this.getFormData(), this.taskKey)
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe({
          next: () => {
            this.form.value?.requiredChanges
              ?.map((requiredChanges) => requiredChanges?.files)
              .forEach((files) =>
                files.forEach((file) => this.store.empDelegate.addReviewAttachment({ [file.uuid]: file.file.name })),
              );
            this.internalSet = false;
            this.isOnEditState = false;
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
        ...(this.form.value.type === 'ACCEPTED' || this.form.value.type === 'REJECTED'
          ? this.form.value.type === 'ACCEPTED'
            ? {
                variationScheduleItems:
                  this.form.value.variationScheduleItems?.map((variationScheduleItem) => variationScheduleItem.item) ??
                  null,
              }
            : {}
          : {
              requiredChanges:
                this.form.value.requiredChanges?.map((changes) => ({
                  ...changes,
                  files: changes?.files.map((doc: FileUpload) => doc.uuid),
                })) ?? null,
            }),
      },
    };
  }
}
