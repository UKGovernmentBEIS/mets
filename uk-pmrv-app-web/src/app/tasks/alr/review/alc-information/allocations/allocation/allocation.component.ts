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
  standalone: true,
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
        [isAlr]="true"></app-preliminary-allocation-details-template>
    </app-alr-task-common>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [allocationFormProvider],
})
export class AlrAllocationComponent {
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
