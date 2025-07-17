import { ChangeDetectionStrategy, Component, ElementRef, Inject, InjectionToken, ViewChild } from '@angular/core';
import { FormArray, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { AviationAerInPersonSiteVisitType, OpinionStatementFormProvider } from '../opinion-statement-form.provider';
import { OpinionStatementInPersonVisitFormProvider } from './opinion-statement-in-person-visit-form.provider';

export const AER_SITE_VISIT_FORM = new InjectionToken<{ form: AviationAerInPersonSiteVisitType; reset: () => void }>(
  'In Person Site Visit form',
);

@Component({
  selector: 'app-opinion-statement-in-person-visit-form',
  templateUrl: './opinion-statement-in-person-visit-form.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [
    {
      provide: AER_SITE_VISIT_FORM,
      useClass: OpinionStatementInPersonVisitFormProvider,
    },
    DestroySubject,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementInPersonVisitFormComponent {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;

  protected inPersonSiteVisitGroup = this.formProviderInPersonSiteVisit.form;
  protected visitDatesCtrl = this.formProviderInPersonSiteVisit.visitDatesCtrl;

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: OpinionStatementFormProvider,
    @Inject(AER_SITE_VISIT_FORM)
    protected readonly formProviderInPersonSiteVisit: OpinionStatementInPersonVisitFormProvider,
    private fb: FormBuilder,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  addVisitDatesCtrl() {
    this.formProviderInPersonSiteVisit.addVisitDatesCtrl();
  }

  removeVisitDatesCtrl(index: number) {
    this.formProviderInPersonSiteVisit.removeVisitDatesCtrl(index);
  }

  onSubmit() {
    if (this.inPersonSiteVisitGroup.invalid) return;

    this.formProvider.inPersonSiteVisitGroup.patchValue({
      ...this.inPersonSiteVisitGroup.value,
    });

    (this.formProvider.inPersonSiteVisitGroup.controls.visitDates as FormArray).clear();
    this.formProvider.inPersonSiteVisitGroup.setControl(
      'visitDates',
      this.fb.array(this.inPersonSiteVisitGroup.value.visitDates ?? []),
    );

    const value = this.formProvider.getFormValue();

    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ opinionStatement: { ...value } }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setOpinionStatement({ ...value });
        this.router.navigate(['../..', 'summary'], { relativeTo: this.route });
      });
  }
}
