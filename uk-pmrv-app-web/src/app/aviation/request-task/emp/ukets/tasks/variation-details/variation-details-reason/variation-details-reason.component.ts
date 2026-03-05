import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { VariationDetailsFormProvider } from '@aviation/request-task/emp/ukets/tasks/variation-details/variation-details-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { VariationDetailsReasonTypePipe } from '@aviation/shared/pipes/variation-details-reason-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-variation-details-reason',
  standalone: true,
  imports: [RouterModule, SharedModule, ReturnToLinkComponent, VariationDetailsReasonTypePipe],
  templateUrl: './variation-details-reason.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsReasonComponent implements OnInit, OnDestroy {
  form = this.formProvider.form;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: VariationDetailsFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private backLinkService: BackLinkService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    const variationDetails = this.formProvider.getVariationDetailsFormValue();
    const variationRegulatorLedReason = this.formProvider.getVariationRegulatorLedReasonFormValue();

    this.store.empUkEtsDelegate
      .saveEmp({ reasonRegulatorLed: variationRegulatorLedReason }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empUkEtsDelegate.setVariationDetails(variationDetails, variationRegulatorLedReason);
        this.router.navigate(['../summary'], { relativeTo: this.route });
      });
  }
}
