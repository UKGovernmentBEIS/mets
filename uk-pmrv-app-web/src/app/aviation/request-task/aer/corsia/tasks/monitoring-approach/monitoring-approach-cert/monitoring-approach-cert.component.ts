import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AviationAerCorsiaMonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaMonitoringApproach } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-approach-type',
  templateUrl: './monitoring-approach-cert.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachCertComponent {
  private formProvider = inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  form = this.formProvider.getFormAsGroup('certUsed') as FormGroup<{ certUsed: FormControl<boolean | null> }>;

  onSubmit() {
    const nextRoute = this.form.controls.certUsed.value ? 'cert-usage' : 'fuel-usage';
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
    const formCtrl = this.form.controls.certUsed;
    const providerCtrl = this.formProvider.form.controls.certUsed;

    if (formCtrl.value !== providerCtrl.value) {
      return {
        certUsed: formCtrl.value,
      };
    }

    return {
      ...this.formProvider.form.value,
      certUsed: formCtrl.value,
    } as AviationAerCorsiaMonitoringApproach;
  }
}
