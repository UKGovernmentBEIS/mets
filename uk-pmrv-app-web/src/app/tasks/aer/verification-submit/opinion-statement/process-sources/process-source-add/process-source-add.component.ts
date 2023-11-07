import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { noProcessSourceText } from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-process-source-add',
  template: ` <app-aer-task-review heading="Add a process source" returnToLink="../../..">
    <app-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(isEditable$ | async) === false"
    >
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-two-thirds">
          <div formControlName="processSource" govuk-text-input></div>
        </div>
      </div>
    </app-wizard-step>
  </app-aer-task-review>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessSourceAddComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;

  form = this.fb.group({
    processSource: [
      { value: null, disabled: !this.store.getValue().isEditable },
      { validators: [GovukValidators.required(noProcessSourceText)] },
    ],
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: CommonTasksStore,
    private readonly aerService: AerService,
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.aerService
      .getPayload()
      .pipe(
        first(),
        map(
          (payload) =>
            (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.opinionStatement,
        ),
        switchMap((opinionStatement) => {
          const processSource = this.form.get('processSource').value;
          opinionStatement.processSources = opinionStatement?.processSources ?? [];
          opinionStatement.processSources.push(processSource);
          return this.aerService.postVerificationTaskSave(
            {
              opinionStatement: {
                ...opinionStatement,
              },
            },
            false,
            'opinionStatement',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
