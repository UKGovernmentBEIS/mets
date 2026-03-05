import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { OperatorDetailsOperatingLicenseTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-operating-license-template/operator-details-operating-license-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { IssuingAuthoritiesService, OperatingLicense } from 'pmrv-api';

import { BaseOperatorDetailsComponent } from '../base-operator-details.component';
import { OperatorDetailsFormProvider } from '../operator-details-form.provider';

@Component({
  selector: 'app-operator-details-operating-license-page',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    OperatorDetailsOperatingLicenseTemplateComponent,
  ],
  templateUrl: './operator-details-operating-license.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsOperatingLicenseComponent extends BaseOperatorDetailsComponent implements OnInit {
  form = this.getform('operatingLicense');
  issuingAuthorityOptions$ = this.getIssuingAuthorityOptions(this.issuingAuthorityService);

  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
    private issuingAuthorityService: IssuingAuthoritiesService,
  ) {
    super(router, route, pendingRequestService, formProvider, store, destroy$);
  }

  onSubmit() {
    const licenseExist = this.form.value.licenseExist as OperatingLicense['licenseExist'];

    if (!licenseExist) {
      this.form.patchValue({ licenseNumber: null, issuingAuthority: null });
    }

    const operatorDetails = { ...this.operatorDetails, operatingLicense: this.form.value };

    this.submitForm('operatingLicense', operatorDetails, 'organisation-structure');
  }
}
