import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { parseCsv } from '@aviation/request-task/util';
import { OperatorDetailsFlightIdentificationTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-flight-identification-template/operator-details-flight-identification-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { FlightIdentification } from 'pmrv-api';

import { BaseOperatorDetailsComponent } from '../base-operator-details.component';
import { OperatorDetailsCorsiaFormProvider } from '../operator-details-form.provider';

@Component({
  selector: 'app-operator-details-flight-identification-page',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    ReturnToLinkComponent,
    OperatorDetailsFlightIdentificationTemplateComponent,
  ],
  templateUrl: './operator-details-flight-identification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsFlightIdentificationComponent extends BaseOperatorDetailsComponent {
  form = this.getform('flightIdentification');
  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsCorsiaFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {
    super(router, route, pendingRequestService, formProvider, store, destroy$);
  }

  onSubmit() {
    const flightIdentificationType = this.form.value
      .flightIdentificationType as FlightIdentification['flightIdentificationType'];

    if (flightIdentificationType === 'AIRCRAFT_REGISTRATION_MARKINGS') {
      this.form.controls.icaoDesignators.patchValue(null);
    } else {
      this.form.controls.aircraftRegistrationMarkings.patchValue(null);
    }

    const payload = {
      flightIdentification: {
        ...this.form.value,
        aircraftRegistrationMarkings: parseCsv(this.form.value.aircraftRegistrationMarkings as string),
      },
    };

    this.submitForm('flightIdentification', payload, '../air-operating-certificate');
  }
}
