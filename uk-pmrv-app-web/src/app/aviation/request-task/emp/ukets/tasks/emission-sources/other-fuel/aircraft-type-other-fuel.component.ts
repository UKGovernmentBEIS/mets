import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmissionSourcesFormModel } from '../emission-sources-form.model';
import { addMultipleMethodsControl, hasMoreThanOneMonitoringMethod } from '../emission-sources-form.provider';

@Component({
  selector: 'app-aircraft-type-other-fuel',
  templateUrl: './aircraft-type-other-fuel.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterModule, SharedModule, ReturnToLinkComponent],
})
export class AircraftTypeOtherFuelComponent {
  emissionSourcesForm = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  fb = inject(FormBuilder);
  form = this.fb.group({
    otherFuelExplanation: this.emissionSourcesForm.controls.otherFuelExplanation,
  });
  router = inject(Router);
  store = inject(RequestTaskStore);
  pendingRequestService = inject(PendingRequestService);
  route = inject(ActivatedRoute);
  onSubmit() {
    const value = { ...this.emissionSourcesForm.getRawValue(), ...this.form.getRawValue() };
    this.store.empUkEtsDelegate
      .saveEmp({ emissionSources: value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        if (isFUMM(this.store.getValue().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadUkEts)) {
          if (hasMoreThanOneMonitoringMethod(this.emissionSourcesForm)) {
            addMultipleMethodsControl(this.emissionSourcesForm, this.fb);
            this.router.navigate(['../multiple-methods'], { relativeTo: this.route });
          } else {
            this.router.navigate(['../monitoring-methodology'], { relativeTo: this.route });
          }
        } else {
          this.emissionSourcesForm.updateValueAndValidity();
          this.router.navigate(['../summary'], { relativeTo: this.route });
        }
      });
  }
}
