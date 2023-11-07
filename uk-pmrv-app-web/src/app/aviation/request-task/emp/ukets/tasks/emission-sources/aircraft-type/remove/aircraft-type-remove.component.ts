import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { TASK_FORM_PROVIDER } from '../../../../../../task-form.provider';
import { EmissionSourcesFormModel } from '../../emission-sources-form.model';
import {
  hasMoreThanOneMonitoringMethod,
  hasOtherFuelType,
  removeMultipleMethodsControl,
  removeOtherFuelTypeExplanationControl,
} from '../../emission-sources-form.provider';

@Component({
  selector: 'app-aircraft-type-remove',
  templateUrl: './aircraft-type-remove.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterModule, SharedModule],
})
export class AircraftTypeRemoveComponent {
  route = inject(ActivatedRoute);
  router = inject(Router);
  store = inject(RequestTaskStore);
  pendingRequestService = inject(PendingRequestService);
  emissionSourcesForm = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  onDelete(): void {
    const removeIndexParam = this.route.snapshot.queryParamMap.get('aircraftTypeIndex');
    const removeIndex = Number(removeIndexParam);
    if (removeIndex >= 0) {
      this.emissionSourcesForm.controls.aircraftTypes.removeAt(Number(removeIndex));
      if (!hasOtherFuelType(this.emissionSourcesForm)) {
        removeOtherFuelTypeExplanationControl(this.emissionSourcesForm);
      }
      if (!hasMoreThanOneMonitoringMethod(this.emissionSourcesForm)) {
        removeMultipleMethodsControl(this.emissionSourcesForm);
      }
      if (!this.emissionSourcesForm.controls.aircraftTypes.length) {
        this.emissionSourcesForm.controls.aircraftTypes.markAsUntouched();
      }
      this.store.empUkEtsDelegate
        .saveEmp({ emissionSources: this.emissionSourcesForm.getRawValue() } as any, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../'], {
            relativeTo: this.route,
            queryParamsHandling: 'merge',
            queryParams: { change: true },
          });
        });
    }
  }
}
