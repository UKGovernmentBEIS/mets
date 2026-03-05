import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormArray, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { HasSubsidiaryCompaniesTemplateComponent } from '@aviation/shared/components/emp-corsia/has-subsidiary-companies';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { BaseOperatorDetailsComponent } from '../../base-operator-details.component';
import { OperatorDetailsCorsiaFormProvider } from '../../operator-details-form.provider';

@Component({
  selector: 'app-has-subsidiary-company',
  templateUrl: './has-subsidiary-company.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [HasSubsidiaryCompaniesTemplateComponent, ReturnToLinkComponent],
})
export class HasSubsidiaryCompanyComponent extends BaseOperatorDetailsComponent {
  form = this.fb.group({
    subsidiaryCompanyExist: this.getform('subsidiaryCompanyExist'),
  });

  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsCorsiaFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
    private readonly fb: FormBuilder,
  ) {
    super(router, route, pendingRequestService, formProvider, store, destroy$);
  }

  onSubmit() {
    const subsidiaryCompanyExist = this.form.value?.subsidiaryCompanyExist;

    this.formProvider.initiateSubsidiaryCompaniesForm();

    if (subsidiaryCompanyExist) {
      this.submitForm(
        'subsidiaryCompanyExist',
        { subsidiaryCompanyExist },
        this.formProvider.form.value?.subsidiaryCompanies?.length > 1 ? '../list' : '../add',
      );
    } else if (subsidiaryCompanyExist === false) {
      (this.getform('subsidiaryCompanies') as unknown as FormArray).clear();
      this.submitForm('subsidiaryCompanyExist', { subsidiaryCompanyExist }, '../../summary');
    }
  }
}
