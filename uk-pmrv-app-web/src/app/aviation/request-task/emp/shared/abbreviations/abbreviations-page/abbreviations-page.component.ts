import { Component, ElementRef, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { empHeaderTaskMap } from '../../../shared/util/emp.util';
import { AbbreviationsFormComponent } from '../abbreviations-form';
import { AbbreviationsFormProvider } from '../abbreviations-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-abbreviations-page',
  standalone: true,
  imports: [SharedModule, AbbreviationsFormComponent, ReturnToLinkComponent],
  templateUrl: './abbreviations-page.component.html',
})
export class AbbreviationsPageComponent implements OnInit, OnDestroy {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<AbbreviationsFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);
  private backLinkService = inject(BackLinkService);

  form = this.formProvider.form;
  readonly empHeaderTaskMap = empHeaderTaskMap;

  ngOnInit(): void {
    this.backLinkService.show('..');
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    this.store.empDelegate
      .saveEmp({ abbreviations: this.form.value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empDelegate.setAbbreviations(this.formProvider.getFormValue());
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }
}
