import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DoalService } from '@tasks/doal/core/doal.service';
import { DOAL_TASK_FORM } from '@tasks/doal/core/doal-task-form.token';

import { preliminaryAllocationFormProvider } from './preliminary-allocation-form.provider';

@Component({
  selector: 'app-preliminary-allocation',
  template: `
    <app-doal-task [breadcrumb]="true">
      <app-preliminary-allocation-details-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditable]="editable$ | async"
        [isEditing]="createMode === false"></app-preliminary-allocation-details-template>
      <a govukLink routerLink="..">Return to: Provide allocation for each sub installation</a>
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
      this.doalService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.doalService.saveDoal(
              {
                activityLevelChangeInformation: {
                  ...payload.doal.activityLevelChangeInformation,
                  preliminaryAllocations: this.createMode
                    ? [...(payload.doal.activityLevelChangeInformation?.preliminaryAllocations ?? []), this.form.value]
                    : payload.doal.activityLevelChangeInformation?.preliminaryAllocations?.map(
                        (preliminaryAllocation, idx) =>
                          idx === Number(this.index) ? this.form.value : preliminaryAllocation,
                      ),
                },
              },
              this.route.snapshot.data.sectionKey,
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
    }
  }
}
