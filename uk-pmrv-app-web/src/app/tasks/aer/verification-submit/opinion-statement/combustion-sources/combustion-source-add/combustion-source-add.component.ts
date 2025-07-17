import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { noCombustionSourceText } from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload, OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-combustion-source-add',
  template: `
    <app-aer-task-review heading="Add a combustion source" returnToLink="../../..">
      <app-wizard-step
        (formSubmit)="onSubmit()"
        [formGroup]="form"
        submitText="Continue"
        [hideSubmit]="(isEditable$ | async) === false">
        <div class="govuk-grid-row">
          <div class="govuk-grid-column-two-thirds">
            <div formControlName="combustionSource" govuk-text-input></div>
          </div>
        </div>
      </app-wizard-step>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CombustionSourceAddComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;

  form = this.fb.group({
    combustionSource: [
      { value: null, disabled: !this.store.getValue().isEditable },
      { validators: [GovukValidators.required(noCombustionSourceText)] },
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
          const combustionSource = this.form.get('combustionSource').value;
          opinionStatement.combustionSources = opinionStatement?.combustionSources ?? [];
          opinionStatement.combustionSources.push(combustionSource);
          return this.aerService.postVerificationTaskSave(
            {
              opinionStatement: {
                ...opinionStatement,
              } as OpinionStatement,
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
