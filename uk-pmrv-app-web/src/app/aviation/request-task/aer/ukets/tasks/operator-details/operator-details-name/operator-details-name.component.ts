import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { OperatorDetailsNameTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-name-template/operator-details-name-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseOperatorDetailsComponent } from '../base-operator-details.component';
import { OperatorDetailsFormProvider } from '../operator-details-form.provider';

@Component({
  selector: 'app-operator-details-name-page',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    OperatorDetailsNameTemplateComponent,
  ],
  templateUrl: './operator-details-name.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsNameComponent extends BaseOperatorDetailsComponent implements OnInit {
  form = this.getform('operatorName');

  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {
    super(router, route, pendingRequestService, formProvider, store, destroy$);
  }

  onSubmit() {
    const operatorDetails = { ...this.operatorDetails, ...this.form.value };
    this.submitForm('operatorName', operatorDetails, 'flight-identification');
  }
}
