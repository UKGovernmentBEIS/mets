import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  AviationAerCorsiaFuelUseMonitoringDetailsFormModel,
  AviationAerCorsiaMonitoringApproachFormProvider,
} from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { FuelDensityTypePipe } from '@aviation/shared/pipes/fuel-density-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaMonitoringApproach } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-approach-type',
  templateUrl: './monitoring-approach-fuel-usage.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, FuelDensityTypePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachFuelUsageComponent {
  private formProvider = inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  form = this.formProvider.getFormAsGroup(
    'fuelUseMonitoringDetails',
  ) as FormGroup<AviationAerCorsiaFuelUseMonitoringDetailsFormModel>;

  onSubmit() {
    const nextRoute = this.form.controls?.blockHourUsed?.value ? '../fuel-allocation-block-hour' : '../summary';
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
      fuelUseMonitoringDetails: {
        ...this.form.value,
        aircraftTypeDetails:
          this.form.controls?.blockHourUsed.value &&
          this.formProvider.form.controls.fuelUseMonitoringDetails.controls?.aircraftTypeDetails?.value
            ? this.formProvider.form.controls.fuelUseMonitoringDetails.controls?.aircraftTypeDetails?.value
            : null,
      },
    } as AviationAerCorsiaMonitoringApproach;
  }
}
