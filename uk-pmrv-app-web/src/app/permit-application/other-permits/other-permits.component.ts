import {
  AfterContentChecked,
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, takeUntil } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../core/services/destroy-subject.service';
import { WizardStepComponent } from '../../shared/wizard/wizard-step.component';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { createAnotherPermit, otherPermitsFormProvider } from './other-permits-form.provider';

@Component({
  selector: 'app-other-permits',
  templateUrl: './other-permits.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [otherPermitsFormProvider, DestroySubject],
})
export class OtherPermitsComponent
  extends SectionComponent
  implements AfterViewInit, PendingRequest, AfterContentChecked
{
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

  private permitsLength = this.permits.length;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private cd: ChangeDetectorRef,
  ) {
    super(store, router, route);
  }

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  get permits(): UntypedFormArray {
    return this.form.get('envPermitOrLicences') as UntypedFormArray;
  }

  onSubmit(): void {
    this.store
      .postTask('environmentalPermitsAndLicences', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.navigateSubmitSection('summary', 'details'));
  }

  ngAfterContentChecked(): void {
    this.cd.detectChanges();
  }
  ngAfterViewInit(): void {
    this.removeButtons.changes.pipe(takeUntil(this.destroy$)).subscribe((buttons) => {
      if (buttons.length > this.permitsLength) {
        buttons.last.nativeElement.focus();
      }
      this.permitsLength = buttons.length;
    });
  }

  addOtherPermit(): void {
    this.permits.push(createAnotherPermit());
    this.wizardStepComponent.isSummaryDisplayedSubject
      .pipe(
        first(),
        filter((value) => value),
      )
      .subscribe(() => this.permits.at(this.permits.length - 1).markAllAsTouched());
  }
}
