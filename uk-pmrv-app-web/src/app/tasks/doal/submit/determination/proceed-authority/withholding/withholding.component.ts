import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { DoalService } from '../../../../core/doal.service';
import { DOAL_TASK_FORM } from '../../../../core/doal-task-form.token';
import { withholdingFormProvider } from './withholding-form.provider';

@Component({
  selector: 'app-withholding',
  templateUrl: './withholding.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [withholdingFormProvider],
})
export class WithholdingComponent {
  private readonly nextWizardStep = 'official-notice';
  today = new Date();
  editable$: Observable<boolean> = this.doalService.isEditable$;

  constructor(
    @Inject(DOAL_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../', this.nextWizardStep], {
        relativeTo: this.route,
      });
    } else {
      this.doalService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.doalService.saveDoal(
              {
                determination: {
                  ...payload.doal.determination,
                  ...this.form.value,
                  withholdingAllowancesNotice: this.form.value.hasWithholdingOfAllowances
                    ? this.form.value.withholdingAllowancesNotice
                    : undefined,
                } as any,
              },
              this.route.snapshot.data.sectionKey,
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(['../', this.nextWizardStep], {
            relativeTo: this.route,
          }),
        );
    }
  }
}
