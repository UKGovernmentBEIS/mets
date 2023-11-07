import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { OperatorDetailsAirOperatingCertificateTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-air-operating-certificate-template/operator-details-air-operating-certificate-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AirOperatingCertificate, IssuingAuthoritiesService } from 'pmrv-api';

import { BaseOperatorDetailsComponent } from '../base-operator-details.component';
import { OperatorDetailsCorsiaFormProvider } from '../operator-details-form.provider';

@Component({
  selector: 'app-operator-details-air-operating-certificate-page',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    OperatorDetailsAirOperatingCertificateTemplateComponent,
    ReturnToLinkComponent,
  ],
  templateUrl: './operator-details-air-operating-certificate.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsAirOperatingCertificateComponent extends BaseOperatorDetailsComponent {
  form = this.getform('airOperatingCertificate');
  issuingAuthorityOptions$ = this.getIssuingAuthorityOptions(this.issuingAuthorityService);
  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsCorsiaFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
    private issuingAuthorityService: IssuingAuthoritiesService,
  ) {
    super(router, route, pendingRequestService, formProvider, store, destroy$);
  }

  onSubmit() {
    const certificateExist = this.form.value.certificateExist as AirOperatingCertificate['certificateExist'];

    if (!certificateExist) {
      this.form.patchValue({ certificateNumber: null, issuingAuthority: null, certificateFiles: [] });
    }

    const payload = {
      airOperatingCertificate: {
        ...this.form.value,
        certificateFiles: this.form.value.certificateFiles?.map((doc: FileUpload) => doc.uuid),
      },
    };
    this.submitForm('airOperatingCertificate', payload, '../organisation-structure');
  }
}
