/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { abbreviationsFormProvider } from './abbreviations-form.provider';

@Component({
  selector: 'app-abbreviations',
  template: `
    <app-permit-task reviewGroupTitle="Additional information" reviewGroupUrl="additional-info" [breadcrumb]="true">
      <app-abbreviations-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditable]="store.isEditable$ | async"
        caption="Additional information"
      ></app-abbreviations-template>
      <app-list-return-link
        reviewGroupTitle="Additional information"
        reviewGroupUrl="additional-info"
      ></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [abbreviationsFormProvider],
})
export class AbbreviationsComponent extends SectionComponent {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    this.store
      .postTask('abbreviations', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.navigateSubmitSection('summary', 'additional-info'));
  }
}
