import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { parseCsv } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { empQuery } from '../../emp.selectors';
import { BlockHourProceduresFormProvider } from '../block-hour-procedures-form.provider';

@Component({
  selector: 'app-specific-burn-fuel-calculation',
  templateUrl: './specific-burn-fuel-calculation.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, NgIf, NgFor, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SpecificBurnFuelCalculationComponent {
  form = this.formProvider.specificBurnFuel;
  isCorsia = toSignal(this.store.pipe(empQuery.selectIsCorsia));

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: BlockHourProceduresFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  disableSpecificBurnFuelConditionally() {
    const { fuelBurnCalculationTypes } = this.form.value;

    if (fuelBurnCalculationTypes?.includes('CLEAR_DISTINGUISHION')) {
      this.form.controls.assignmentAndAdjustment.enable();
      this.form.controls.clearDistinguishionIcaoAircraftDesignators.enable();
    } else {
      this.form.controls.assignmentAndAdjustment.disable();
      this.form.controls.clearDistinguishionIcaoAircraftDesignators.disable();
    }

    if (fuelBurnCalculationTypes?.includes('NOT_CLEAR_DISTINGUISHION')) {
      this.form.controls.notClearDistinguishionIcaoAircraftDesignators.enable();
    } else {
      this.form.controls.notClearDistinguishionIcaoAircraftDesignators.disable();
    }
  }

  onSubmit() {
    const data = {
      ...this.formProvider.getFormValue(),
      ...this.form.value,
      assignmentAndAdjustment: this.form.value.assignmentAndAdjustment,
      clearDistinguishionIcaoAircraftDesignators: parseCsv(
        this.form.value.clearDistinguishionIcaoAircraftDesignators as string,
        true,
      ),
      notClearDistinguishionIcaoAircraftDesignators: parseCsv(
        this.form.value.notClearDistinguishionIcaoAircraftDesignators as string,
        true,
      ),
    };

    this.store.empDelegate
      .saveEmp({ blockHourMethodProcedures: data }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empDelegate.setBlockHourMethodProcedures(data);

        this.router.navigate(['./block-hours-measurement'], {
          relativeTo: this.route,
        });
      });
  }
}
