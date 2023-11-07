import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { ReturnOfAllowancesService } from '../../core/return-of-allowances.service';
import { RETURN_OF_ALLOWANCES_TASK_FORM } from '../../core/return-of-allowances-task-form.token';
import { summaryFormFactory } from './summary-form.provider';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [summaryFormFactory],
})
export class SummaryComponent implements OnInit {
  notification = this.router.getCurrentNavigation()?.extras?.state?.notification;
  isEditable$ = this.returnOfAllowancesService.isEditable$;
  displayErrorSummary$ = new BehaviorSubject(false);
  payload$ = this.returnOfAllowancesService.getPayload().pipe(
    first(),
    map((payload) => (payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload).returnOfAllowances),
  );

  constructor(
    @Inject(RETURN_OF_ALLOWANCES_TASK_FORM) readonly form: FormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly returnOfAllowancesService: ReturnOfAllowancesService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
      return;
    }
  }

  onSubmit(): void {
    this.returnOfAllowancesService
      .saveSectionStatus(true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
