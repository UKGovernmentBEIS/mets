import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, filter, map, Observable, takeUntil } from 'rxjs';

import { AerVerificationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-verification-review-decision-group-summary/aer-verification-review-decision-group-summary.component';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AerVerifyCorsiaTaskKey, AerVerifyTaskKey, requestTaskQuery, RequestTaskStore } from '../../../store';
import { aerQuery } from '../aer.selectors';
import { AerUkEtsReviewGroup } from '../util/aer.util';
import { aerVerifyReviewGroupMap } from '../util/aer-verify-tasks.util';
import { AerVerificationReviewDecisionGroupFormProvider } from './aer-verification-review-decision-group-form-provider';
import { AerVerificationReviewDecisionGroupFormComponent } from './form/aer-verification-review-decision-group-form.component';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-aer-verification-review-decision-group',
  standalone: true,
  imports: [
    SharedModule,
    AerVerificationReviewDecisionGroupFormComponent,
    AerVerificationReviewDecisionGroupSummaryComponent,
  ],
  templateUrl: './aer-verification-review-decision-group.component.html',
  providers: [DestroySubject],
})
export class AerVerificationReviewDecisionGroupComponent implements OnInit, OnDestroy {
  @Input() taskKey: AerVerifyTaskKey | AerVerifyCorsiaTaskKey;
  @Input() addValidator: ValidatorFn;

  allowedReviewEditableActions = ['AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION'];

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
  private groupKey: AerUkEtsReviewGroup;

  constructor(
    public store: RequestTaskStore,
    private destroy$: DestroySubject,
    private pendingRequestService: PendingRequestService,
    private route: ActivatedRoute,
    private router: Router,
    readonly formProvider: AerVerificationReviewDecisionGroupFormProvider,
  ) {}

  ngOnInit(): void {
    this.groupKey = aerVerifyReviewGroupMap[this.taskKey];

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
      this.store.aerDelegate
        .saveAerReviewDecision(this.getFormData(), this.taskKey as AerVerifyTaskKey & AerVerifyCorsiaTaskKey)
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe({
          next: () => {
            this.internalSet = false;
            this.isOnEditState = false;

            const path = this.router.url.split('/').at(-1) === 'summary' ? '../../../../..' : '../../..';

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
      },
    };
  }
}
