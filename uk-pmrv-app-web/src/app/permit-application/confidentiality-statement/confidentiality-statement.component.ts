/* eslint-disable @angular-eslint/component-max-inline-declarations */
import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { DestroySubject } from '../../core/services/destroy-subject.service';
import { WizardStepComponent } from '../../shared/wizard/wizard-step.component';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { confidentialityStatementFormProvider } from './confidentiality-statement-form.provider';

@Component({
  selector: 'app-confidentiality-statement',
  template: `
    <app-permit-task reviewGroupTitle="Confidentiality" reviewGroupUrl="confidentiality" [breadcrumb]="true">
      <app-confidentiality-statement-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditable]="store.isEditable$ | async"
        [caption]="'Confidentiality statement'"
      >
      </app-confidentiality-statement-template>
      <app-list-return-link reviewGroupTitle="Confidentiality" reviewGroupUrl="confidentiality"></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [confidentialityStatementFormProvider, DestroySubject],
})
export class ConfidentialityStatementComponent extends SectionComponent {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

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
      .postTask('confidentialityStatement', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.navigateSubmitSection('summary', 'confidentiality'));
  }
}
