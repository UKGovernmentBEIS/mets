import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import {
  activityGroupMap,
  activityHintMap,
  formGroupOptions,
  unitOptions,
} from '@shared/components/regulated-activities/regulated-activities-form-options';
import { originalOrder } from '@shared/keyvalue-order';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { regulatedActivityAddFormProvider } from '@tasks/aer/verification-submit/opinion-statement/regulated-activities/regulated-activity-add/regulated-activity-add-form.provider';

import { AerApplicationVerificationSubmitRequestTaskPayload, RegulatedActivity } from 'pmrv-api';

@Component({
  selector: 'app-regulated-activity-add',
  templateUrl: './regulated-activity-add.component.html',
  providers: [regulatedActivityAddFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatedActivityAddComponent implements OnInit {
  readonly originalOrder = originalOrder;
  readonly activityGroups = formGroupOptions;
  activityGroupMap = activityGroupMap;
  unitOptions = unitOptions;
  activityHintMap = activityHintMap;
  changing = this.router.getCurrentNavigation()?.extras?.state?.changing;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    readonly location: Location,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.enableOptionalFields();
  }

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
          const type = (this.form.get('activity').value ??
            this.form.get('activityCategory').value) as RegulatedActivity['type'];
          opinionStatement.regulatedActivities = opinionStatement?.regulatedActivities ?? [];
          opinionStatement.regulatedActivities.push(type);
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

  private enableOptionalFields() {
    this.form
      .get('activityCategory')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.form.get('activityCategory')) {
          this.form.get('activity').setValue(null);
          this.form.get('activity').enable();
        }
      });
  }
}
