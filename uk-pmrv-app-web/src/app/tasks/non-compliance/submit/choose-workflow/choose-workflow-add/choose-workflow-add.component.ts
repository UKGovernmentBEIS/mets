import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { NonComplianceApplicationSubmitRequestTaskPayload, RequestInfoDTO } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { NonComplianceService } from '../../../core/non-compliance.service';
import { NON_COMPLIANCE_TASK_FORM } from '../../../core/non-compliance-form.token';
import { chooseWorkflowAddFormProvider } from './choose-workflow-add-form.provider';

@Component({
  selector: 'app-choose-workflow-add',
  templateUrl: './choose-workflow-add.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [chooseWorkflowAddFormProvider],
})
export class ChooseWorkflowAddComponent implements OnInit {
  private readonly nextWizardStep = 'choose-workflow';

  index = this.route.snapshot.paramMap.get('index');
  createMode = this.index === null;

  availableRequests$ = this.nonComplianceService.payload$.pipe(
    map((payload) => (payload as NonComplianceApplicationSubmitRequestTaskPayload)?.availableRequests),
  );

  selectedRequests$ = this.nonComplianceService.payload$.pipe(
    map((payload) => (payload as NonComplianceApplicationSubmitRequestTaskPayload)?.selectedRequests),
  );

  requests: Array<RequestInfoDTO>;

  constructor(
    @Inject(NON_COMPLIANCE_TASK_FORM) readonly form: UntypedFormGroup,
    readonly nonComplianceService: NonComplianceService,
    readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  ngOnInit(): void {
    combineLatest([this.selectedRequests$, this.availableRequests$])
      .pipe(
        first(),
        switchMap(([selectedRequests, availableRequests]) => {
          return (this.requests = availableRequests.filter((request) => !selectedRequests.includes(request.id)));
        }),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.nonComplianceService.payload$
      .pipe(
        first(),
        switchMap((payload) => {
          const nonCompliance = payload as NonComplianceApplicationSubmitRequestTaskPayload;
          const selectedRequests = this.createMode
            ? [...(nonCompliance?.selectedRequests ?? []), this.form.value.selectedRequests]
            : nonCompliance.selectedRequests?.map((selectedRequests, idx) =>
                idx === Number(this.index) ? this.form.value.selectedRequests : selectedRequests,
              );
          return this.nonComplianceService.saveNonCompliance(
            {
              ...(nonCompliance as any)?.payload,
              selectedRequests,
            },
            false,
          );
        }),
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..', this.nextWizardStep], { relativeTo: this.route }));
  }

  onContinue() {
    this.router.navigate(['..'], { relativeTo: this.route });
  }
}
