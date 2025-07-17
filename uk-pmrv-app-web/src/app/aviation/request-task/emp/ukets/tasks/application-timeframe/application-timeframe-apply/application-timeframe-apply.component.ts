import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { ApplicationTimeframeApplyFormComponent } from '../application-timeframe-apply-form';
import { ApplicationTimeframeFormProvider } from '../application-timeframe-form.provider';

@Component({
  selector: 'app-application-timeframe-apply',
  templateUrl: './application-timeframe-apply.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ApplicationTimeframeApplyFormComponent, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
  styles: `
    .govuk-heading-s span {
      font-weight: normal;
    }
    .govuk-body {
      margin-bottom: 0;
    }
  `,
})
export class ApplicationTimeframeApplyComponent {
  form = this.formProvider.form;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: ApplicationTimeframeFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.empUkEtsDelegate
      .saveEmp({ applicationTimeframeInfo: this.form.value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empUkEtsDelegate.setApplicationTimeframeInfo(this.form.value);
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }
}
