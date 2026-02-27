import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, take } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { firstYearFormProvider } from './first-year-form.provider';

@Component({
  selector: 'app-first-year',
  standalone: false,
  templateUrl: './first-year.component.html',
  providers: [firstYearFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FirstYearComponent implements PendingRequest {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  permitType = this.store.getValue().requestType;
  determination$ = this.store.getDeterminationType$();
  determinationHeader = this.store.getDeterminationHeader();

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.store
        .pipe(
          first(),
          map((store) => this.getNextStepUrl(store)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    } else {
      const firstYearOfReportingObligation = this.form.value.firstYearOfReportingObligation;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postDetermination(
              {
                ...state.determination,
                firstYearOfReportingObligation,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
          switchMap(() => this.store),
          take(1),
          map((store) => this.getNextStepUrl(store)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    }
  }

  private getNextStepUrl(state: PermitApplicationState): string {
    return state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_REVIEW'
      ? 'log-changes'
      : state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'
        ? 'reason-template'
        : 'answers';
  }
}
