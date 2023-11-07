import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DreService } from '../../core/dre.service';
import { officialNoticeReasonFormProvider } from './official-notice-reason-form.provider';

@Component({
  selector: 'app-official-notice-reason',
  templateUrl: './official-notice-reason.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [officialNoticeReasonFormProvider],
})
export class OfficialNoticeReasonComponent {
  private readonly nextWizardStep = 'monitoring-approaches';
  constructor(
    @Inject(DRE_TASK_FORM) readonly form: UntypedFormGroup,
    readonly dreService: DreService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route });
    } else {
      this.dreService
        .saveDre(
          {
            officialNoticeReason: this.form.value.officialNoticeReason,
          },
          false,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route }));
    }
  }
}
