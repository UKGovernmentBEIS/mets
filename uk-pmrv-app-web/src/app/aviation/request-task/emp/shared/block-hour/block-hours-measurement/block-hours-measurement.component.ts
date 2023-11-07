import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ProcedureFormStepComponent } from '@aviation/request-task/emp/shared/procedure-form-step';
import { ProcedureFormPageHeaderDirective } from '@aviation/request-task/emp/shared/procedure-form-step/procedure-form-page-header.directive';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BlockHourProceduresFormProvider } from '../block-hour-procedures-form.provider';

@Component({
  selector: 'app-block-hours-measurement',
  templateUrl: './block-hours-measurement.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgIf,
    NgFor,
    ReturnToLinkComponent,
    ProcedureFormPageHeaderDirective,
    ProcedureFormStepComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BlockHoursMeasurementComponent {
  form = this.formProvider.blockHoursMeasurement;

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

        this.router.navigate(['../fuel-uplift-supplier-records'], {
          relativeTo: this.route,
        });
      });
  }
}
