import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { VariationDetailsFormProvider } from '@aviation/request-task/emp/corsia/tasks/variation-details/variation-details-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { VariationDetailsReasonTypePipe } from '@aviation/shared/pipes/variation-details-reason-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-variation-details-reason',
  standalone: true,
  imports: [RouterModule, SharedModule, ReturnToLinkComponent, VariationDetailsReasonTypePipe],
  templateUrl: './variation-details-reason.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsReasonComponent {
  form = this.formProvider.form;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: VariationDetailsFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    const variationDetails = this.formProvider.getVariationDetailsFormValue();
    const variationRegulatorLedReason = this.formProvider.getVariationRegulatorLedReasonFormValue();

    this.store.empCorsiaDelegate
      .saveEmp({ reasonRegulatorLed: variationRegulatorLedReason }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empCorsiaDelegate.setVariationDetails(variationDetails, variationRegulatorLedReason);
        this.router.navigate(['../summary'], { relativeTo: this.route });
      });
  }
}
