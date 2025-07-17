import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { preliminaryAllocationFormProvider } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocation/preliminary-allocation-form.provider';
import { DoalService } from '@tasks/doal/core/doal.service';
import { DOAL_TASK_FORM } from '@tasks/doal/core/doal-task-form.token';

import { DoalGrantAuthorityResponse } from 'pmrv-api';

@Component({
  selector: 'app-preliminary-allocation',
  template: `
    <app-doal-task [breadcrumb]="true">
      <app-preliminary-allocation-details-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditable]="editable$ | async"
        [isEditing]="createMode === false"></app-preliminary-allocation-details-template>
      <a govukLink routerLink="..">Return to: Provide authority approved allocation for each sub-installation</a>
    </app-doal-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [preliminaryAllocationFormProvider],
})
export class PreliminaryAllocationComponent {
  editable$: Observable<boolean> = this.doalService.isEditable$;
  index = this.route.snapshot.paramMap.get('index');
  createMode = this.index === null;

  constructor(
    @Inject(DOAL_TASK_FORM) readonly form: UntypedFormGroup,
    readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['..'], { relativeTo: this.route });
    } else {
      this.doalService.authorityPayload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.doalService.saveDoalAuthority(
              {
                authorityResponse: {
                  ...payload.doalAuthority.authorityResponse,
                  preliminaryAllocations: this.createMode
                    ? [
                        ...((payload.doalAuthority.authorityResponse as DoalGrantAuthorityResponse)
                          ?.preliminaryAllocations ?? []),
                        this.form.value,
                      ]
                    : (
                        payload.doalAuthority.authorityResponse as DoalGrantAuthorityResponse
                      )?.preliminaryAllocations?.map((preliminaryAllocation, idx) =>
                        idx === Number(this.index) ? this.form.value : preliminaryAllocation,
                      ),
                } as DoalGrantAuthorityResponse,
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
