import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { allocationFormProvider } from './allocation-form.provider';

@Component({
  selector: 'app-alr-allocation',
  imports: [SharedModule, AlrTaskSharedModule],
  template: `
    <app-alr-task-common
      returnToLink="../../"
      returnLinkTitle="Provide allocation for each sub-installation"
      [breadcrumb]="true">
      <app-preliminary-allocation-details-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditable]="editable$ | async"
        [isEditing]="createMode === false"
        submitText="Continue"
        [isAlr]="true"
        [year]="year"></app-preliminary-allocation-details-template>
    </app-alr-task-common>
  `,
  providers: [allocationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAllocationComponent {
  editable$: Observable<boolean> = this.alrService.isEditable$;
  index = this.route.snapshot.paramMap.get('index');
  createMode = this.index === null;
  year = this.alrService.year;

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
      this.alrService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.alrService.postAlrReview(
              {
                ...payload.regulatorReviewOutcome,
                allocations: this.createMode
                  ? [
                      ...(payload.regulatorReviewOutcome.allocations ?? []),
                      { ...this.form.value, allocationId: payload.regulatorReviewOutcome?.allocations?.length ?? 0 },
                    ]
                  : payload.regulatorReviewOutcome.allocations?.map((preliminaryAllocation, idx) =>
                      idx === Number(this.index)
                        ? { ...this.form.value, allocationId: this.index }
                        : preliminaryAllocation,
                    ),
              },

              'ALC',
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
    }
  }
}
