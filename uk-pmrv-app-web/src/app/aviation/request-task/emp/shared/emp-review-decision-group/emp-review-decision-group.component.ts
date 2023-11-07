import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, filter, map, Observable, takeUntil } from 'rxjs';

import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { EmpIssuanceReviewDecision } from 'pmrv-api';

import { EmpTaskKey, requestTaskQuery, RequestTaskStore } from '../../../store';
import { empQuery } from '../emp.selectors';
import { EmpReviewGroup, empReviewGroupMap } from '../util/emp.util';
import { EmpReviewDecisionGroupFormProvider } from './emp-review-decision-group-form.provider';
import { EmpReviewDecisionGroupFormComponent } from './form/emp-review-decision-group-form.component';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-emp-review-decision-group',
  standalone: true,
  imports: [SharedModule, EmpReviewDecisionGroupFormComponent, EmpReviewDecisionGroupSummaryComponent],
  templateUrl: './emp-review-decision-group.component.html',
  providers: [DestroySubject],
})
export class EmpReviewDecisionGroupComponent implements OnInit, OnDestroy {
  @Input() taskKey: EmpTaskKey;
  @Input() addValidator: ValidatorFn;

  form = this.formProvider.form;
  isEditable$ = this.store.pipe(requestTaskQuery.selectIsEditable);
  isOnEditState = false;
  canEdit$: Observable<boolean>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  decisionData$: Observable<EmpIssuanceReviewDecision>;
  attachments$: Observable<{ [key: string]: string }> = this.store.pipe(empQuery.selectReviewAttachments);

  private internalSet = false;
  private groupKey: EmpReviewGroup; //TODO consider corsia as well

  constructor(
    public store: RequestTaskStore,
    private destroy$: DestroySubject,
    private pendingRequestService: PendingRequestService,
    private route: ActivatedRoute,
    private router: Router,
    readonly formProvider: EmpReviewDecisionGroupFormProvider,
  ) {}

  ngOnInit(): void {
    this.groupKey = empReviewGroupMap[this.taskKey];

    this.canEdit$ = this.store.pipe(
      empQuery.selectReviewSectionsCompleted,
      map((reviewSectionsCompleted) => !reviewSectionsCompleted[this.groupKey]),
    );

    this.decisionData$ = this.store.pipe(
      empQuery.selectReviewDecisions,
      map((reviewDecisions) => reviewDecisions[this.groupKey]),
    );

    this.store
      .pipe(
        empQuery.selectReviewDecisions,
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
      (this.store.empDelegate as EmpUkEtsStoreDelegate) //TODO consider corsia as well
        .saveEmpReviewDecision(this.getFormData(), this.taskKey)
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

            const path = this.router.url.split('/').at(-1) === 'summary' ? '../../../..' : '../../..';

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
                  files: changes?.files.map((doc: FileUpload) => doc.uuid),
                })) ?? [],
            }),
      },
    };
  }
}
