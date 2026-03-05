import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { MonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-monitoring-approach/monitoring-approach-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { FUEL_TYPES_CORSIA } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { MonitoringApproachVerifyCorsiaTypePipe } from '@aviation/shared/pipes/monitoring-approach-verify-corsia-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-monitoring-approach',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, RouterLink, MonitoringApproachVerifyCorsiaTypePipe],
  templateUrl: './monitoring-approach.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachComponent {
  form = this.formProvider.form;
  fuelTypesCorsia = FUEL_TYPES_CORSIA;
  totalEmissionsProvided$ = this.store
    .pipe(aerVerifyCorsiaQuery.selectPayload)
    .pipe(map((payload) => payload.totalEmissionsProvided));
  totalOffsetEmissionsProvided$ = this.store
    .pipe(aerVerifyCorsiaQuery.selectPayload)
    .pipe(map((payload) => payload.totalOffsetEmissionsProvided));

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: MonitoringApproachFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify(
        {
          opinionStatement: this.formProvider.getFormValue(),
        },
        'in progress',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
  }
}
