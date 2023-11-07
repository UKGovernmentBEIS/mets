import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  AviationAerCorsiaCertDetailsFormModel,
  AviationAerCorsiaMonitoringApproachFormProvider,
} from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { CertDetailsFlightTypePipe } from '@aviation/shared/pipes/cert-details-flight-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaMonitoringApproach } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-approach-type',
  templateUrl: './monitoring-approach-cert-usage.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, CertDetailsFlightTypePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachCertUsageComponent {
  private formProvider = inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  form = this.formProvider.getFormAsGroup('certDetails') as FormGroup<AviationAerCorsiaCertDetailsFormModel>;

  onSubmit() {
    const nextRoute =
      this.form.controls.flightType?.value === 'ALL_INTERNATIONAL_FLIGHTS' ? '../summary' : '../fuel-usage';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.store.aerDelegate as AerCorsiaStoreDelegate)
        .saveAer({ monitoringApproach: this.getFormData() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate([nextRoute], { relativeTo: this.route }).then();
        });
    }
  }

  private getFormData(): AviationAerCorsiaMonitoringApproach {
    return {
      ...this.formProvider.form.value,
      certDetails: {
        ...this.form.value,
      },
    } as AviationAerCorsiaMonitoringApproach;
  }
}
