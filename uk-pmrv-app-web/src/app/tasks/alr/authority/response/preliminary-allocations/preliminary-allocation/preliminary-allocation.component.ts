import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRGrantAuthorityResponse } from 'pmrv-api';

import { alrPreliminaryAllocationFormProvider } from './preliminary-allocation-form.provider';

@Component({
  selector: 'app-alr-preliminary-allocation',
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule],
  template: `
    <app-alr-task-common
      returnLink="../../"
      returnLinkTitle="Provide authority approved allocation for each sub-installation"
      [breadcrumb]="true">
      <app-preliminary-allocation-details-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditable]="editable$ | async"
        [isEditing]="createMode === false"
        submitText="Continue"
        newAllocationHeading="Add new allocation"
        [isAlr]="true"></app-preliminary-allocation-details-template>
    </app-alr-task-common>
  `,
  providers: [alrPreliminaryAllocationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrPreliminaryAllocationComponent {
  editable$: Observable<boolean> = this.alrService.isEditable$;
  index = this.route.snapshot.paramMap.get('index');
  createMode = this.index === null;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['..'], { relativeTo: this.route });
    } else {
      this.alrService.authorityPayload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.alrService.postAlrAuthority(
              {
                authorityResponse: {
                  ...payload.authorityReviewOutcome.authorityResponse,
                  preliminaryAllocations: this.createMode
                    ? [
                        ...((payload.authorityReviewOutcome.authorityResponse as ALRGrantAuthorityResponse)
                          ?.preliminaryAllocations ?? []),
                        {
                          ...this.form.value,
                          allocationId:
                            (payload.authorityReviewOutcome.authorityResponse as ALRGrantAuthorityResponse)
                              ?.preliminaryAllocations?.length ?? 0,
                        },
                      ]
                    : (
                        payload.authorityReviewOutcome.authorityResponse as ALRGrantAuthorityResponse
                      )?.preliminaryAllocations?.map((preliminaryAllocation, idx) =>
                        idx === Number(this.index)
                          ? { ...this.form.value, allocationId: this.index }
                          : preliminaryAllocation,
                      ),
                } as ALRGrantAuthorityResponse,
              },
              'authorityResponse',
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
    }
  }
}
