import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { ProcedureFormComponent } from '../../../../shared/procedure-form';
import { EmissionSourcesFormModel } from '../emission-sources-form.model';

@Component({
  selector: 'app-monitoring-methodology',
  templateUrl: './monitoring-methodology.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterModule, SharedModule, ReturnToLinkComponent, ProcedureFormComponent],
})
export class MonitoringMethodologyComponent {
  emissionSourcesForm = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  form = this.emissionSourcesForm.controls.additionalAircraftMonitoringApproach;
  fb = inject(FormBuilder);
  router = inject(Router);
  store = inject(RequestTaskStore);
  pendingRequestService = inject(PendingRequestService);
  route = inject(ActivatedRoute);
  onSubmit() {
    const value = this.emissionSourcesForm.getRawValue();

    (this.store.empDelegate as EmpUkEtsStoreDelegate)
      .saveEmp({ emissionSources: value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['../summary'], { relativeTo: this.route });
      });
  }
}
