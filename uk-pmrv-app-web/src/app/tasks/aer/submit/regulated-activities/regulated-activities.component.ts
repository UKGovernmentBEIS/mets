import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { regulatedActivitiesFormFactory } from '@tasks/aer/submit/regulated-activities/regulated-activities-form.provider';

@Component({
  selector: 'app-regulated-activities',
  templateUrl: './regulated-activities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [regulatedActivitiesFormFactory],
  styles: [
    `
      .float-right {
        float: right;
        cursor: pointer;
      }
    `,
  ],
})
export class RegulatedActivitiesComponent {
  displayErrorSummary$ = new BehaviorSubject(false);
  isSummaryDisplayed$ = this.displayErrorSummary$.pipe(
    tap(() => this.form.updateValueAndValidity()),
    map((displaySummary) => displaySummary || this.form.errors?.missingCrf),
  );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  delete(id: string): void {
    this.router.navigate(['delete', id], { relativeTo: this.route });
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
      return;
    }
    this.aerService
      .postTaskSave({}, {}, true, 'regulatedActivities')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
