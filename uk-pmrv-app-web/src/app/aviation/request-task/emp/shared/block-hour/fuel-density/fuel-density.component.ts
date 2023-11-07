import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { ProcedureFormPageHeaderDirective, ProcedureFormStepComponent } from '../../procedure-form-step';
import { BlockHourProceduresFormProvider } from '../block-hour-procedures-form.provider';

@Component({
  selector: 'app-fuel-density',
  templateUrl: './fuel-density.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgIf,
    NgFor,
    ProcedureFormPageHeaderDirective,
    ProcedureFormStepComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelDensityComponent {
  form = this.formProvider.fuelDensity;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: BlockHourProceduresFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.empDelegate
      .saveEmp({ blockHourMethodProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empDelegate.setBlockHourMethodProcedures(this.formProvider.getFormValue());

        this.router.navigate(['../summary'], {
          relativeTo: this.route,
        });
      });
  }
}
