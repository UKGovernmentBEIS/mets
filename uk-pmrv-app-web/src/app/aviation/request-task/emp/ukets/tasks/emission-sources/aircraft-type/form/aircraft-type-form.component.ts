import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { FuelConsumptionMeasuringMethods } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-consumption-measuring-methods';
import { FUEL_TYPES } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { AircraftTypeDescriptionPipe } from '@aviation/shared/pipes/aircraft-type-description.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { AircraftTypeInfo } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../../task-form.provider';
import { EmissionSourcesFormModel } from '../../emission-sources-form.model';
import {
  addMultipleMethodsControl,
  addOtherFuelTypeExplanationControl,
  hasMoreThanOneMonitoringMethod,
  hasOtherFuelType,
  removeMultipleMethodsControl,
  removeOtherFuelTypeExplanationControl,
} from '../../emission-sources-form.provider';
import { EMP_AIRCRAFT_TYPE_FORM } from '../aircraft-type-form.provider';

@Component({
  selector: 'app-aircraft-type-form',
  templateUrl: './aircraft-type-form.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    #change-aircraft {
      margin-left: 15px;
    }
  `,
  imports: [RouterModule, SharedModule, AircraftTypeDescriptionPipe, ReturnToLinkComponent],
})
export class AircraftTypeFormComponent implements OnInit {
  aircraftTypeForm = inject(EMP_AIRCRAFT_TYPE_FORM).form;
  emissionSourcesForm = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  route = inject(ActivatedRoute);
  router = inject(Router);
  store = inject(RequestTaskStore);
  fb = inject(FormBuilder);
  pendingRequestService = inject(PendingRequestService);
  private backLinkService = inject(BackLinkService);

  fuelTypesControl = this.aircraftTypeForm.controls.fuelTypes;
  fuelTypes = FUEL_TYPES;
  fuelConsumptionMeasuringMethods = FuelConsumptionMeasuringMethods;
  ngOnInit(): void {
    this.backLinkService.show();
  }
  get aircraftTypeText(): AircraftTypeInfo {
    return this.aircraftTypeForm.controls.aircraftTypeInfo.getRawValue();
  }
  get hasFuelConsumptionMeasuringMethod(): boolean {
    return typeof this.aircraftTypeForm.controls.fuelConsumptionMeasuringMethod !== 'undefined';
  }
  get editIndex(): number | null {
    const idx = this.route.snapshot.queryParamMap.get('aircraftTypeIndex');
    if (!idx) return null;
    return Number(idx);
  }

  onSubmit() {
    if (this.aircraftTypeForm.valid) {
      // add or edit form to emissionSourcesForm.aircraftTypes Form Array
      if (typeof this.editIndex === 'number') {
        this.emissionSourcesForm.controls.aircraftTypes
          .at(this.editIndex)
          .patchValue(this.aircraftTypeForm.getRawValue());
      } else {
        this.emissionSourcesForm.controls.aircraftTypes.push(this.aircraftTypeForm);
      }

      if (!hasOtherFuelType(this.emissionSourcesForm)) {
        removeOtherFuelTypeExplanationControl(this.emissionSourcesForm);
      }

      if (hasOtherFuelType(this.emissionSourcesForm)) {
        addOtherFuelTypeExplanationControl(this.emissionSourcesForm, this.fb);
        this.emissionSourcesForm.controls.otherFuelExplanation.updateValueAndValidity();
      }

      if (hasMoreThanOneMonitoringMethod(this.emissionSourcesForm)) {
        addMultipleMethodsControl(this.emissionSourcesForm, this.fb);
        this.emissionSourcesForm.controls.multipleFuelConsumptionMethodsExplanation.updateValueAndValidity();
      }

      if (!hasMoreThanOneMonitoringMethod(this.emissionSourcesForm)) {
        removeMultipleMethodsControl(this.emissionSourcesForm);
      }

      this.store.empUkEtsDelegate
        .saveEmp({ emissionSources: this.emissionSourcesForm.getRawValue() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../'], {
            relativeTo: this.route,
          });
        });
    }
  }
}
