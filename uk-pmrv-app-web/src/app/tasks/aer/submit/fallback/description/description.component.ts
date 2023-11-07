import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { descriptionFormProvider } from '@tasks/aer/submit/fallback/description/description-form.provider';
import { buildTaskData } from '@tasks/aer/submit/fallback/fallback';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-description',
  templateUrl: './description.component.html',
  providers: [descriptionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DescriptionComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = '../total-emissions';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.aerService.postTaskSave(
              buildTaskData(payload, {
                description: this.form.controls.description.value,
              }),
              undefined,
              false,
              'FALLBACK',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
