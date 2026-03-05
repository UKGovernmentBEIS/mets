import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmissionSourcesFormModel } from '../emission-sources-form.model';

@Component({
  selector: 'app-multiple-methods',
  templateUrl: './multiple-methods.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterModule, SharedModule, ReturnToLinkComponent],
})
export class MultipleMethodsComponent {
  emissionSourcesForm = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  fb = inject(FormBuilder);
  form = this.fb.group({
    aircraftTypes: this.emissionSourcesForm.controls.aircraftTypes,
    multipleFuelConsumptionMethodsExplanation:
      this.emissionSourcesForm.controls.multipleFuelConsumptionMethodsExplanation,
  });
  router = inject(Router);
  store = inject(RequestTaskStore);
  pendingRequestService = inject(PendingRequestService);
  route = inject(ActivatedRoute);
  onSubmit() {
    const value = this.emissionSourcesForm.getRawValue();
    this.store.empUkEtsDelegate
      .saveEmp({ emissionSources: value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['../monitoring-methodology'], { relativeTo: this.route });
      });
  }
}
