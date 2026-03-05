import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { resolveDeterminationCompletedUponDecision } from '../core/review-status';
import { REVIEW_DECISION_FORM, reviewDecisionFormProvider } from './decision-form.provider';

@Component({
  selector: 'app-decision',
  templateUrl: './decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reviewDecisionFormProvider],
})
export class DecisionComponent {
  isEditMode$ = new BehaviorSubject(false);
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  reviewDecision$ = this.store.select('reviewDecision');
  isEditable$ = this.store.select('isEditable');

  constructor(
    @Inject(REVIEW_DECISION_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitSurrenderStore,
  ) {}

  onSubmit(): void {
    if (this.form.valid) {
      this.store
        .postReviewDecision(
          {
            type: this.form.controls.type.value,
            details: { notes: this.form.controls.notes.value },
          },
          resolveDeterminationCompletedUponDecision(this.form.controls.type.value, this.store.getState()),
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.isEditMode$.next(false);
        });
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }
}
