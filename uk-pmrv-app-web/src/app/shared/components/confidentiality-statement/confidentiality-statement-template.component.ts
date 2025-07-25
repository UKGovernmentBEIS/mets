import {
  AfterContentChecked,
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { createAnotherSection } from '@shared/components/confidentiality-statement/confidentiality-statement-add';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

@Component({
  selector: 'app-confidentiality-statement-template',
  templateUrl: './confidentiality-statement-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ConfidentialityStatementTemplateComponent implements AfterViewInit, AfterContentChecked {
  @Input() submitText = 'Confirm and complete';
  @Input() form: UntypedFormGroup;
  @Input() isEditable: boolean;
  @Input() caption: string;
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  private sectionsLength = this.sections?.length ?? 0;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    private cd: ChangeDetectorRef,
  ) {}

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  get sections(): UntypedFormArray {
    return this.form?.get('confidentialSections') as UntypedFormArray;
  }

  ngAfterContentChecked(): void {
    this.cd.detectChanges();
  }

  ngAfterViewInit(): void {
    this.removeButtons.changes.pipe(takeUntil(this.destroy$)).subscribe((buttons) => {
      if (buttons.length > this.sectionsLength) {
        buttons.last.nativeElement.focus();
      }
      this.sectionsLength = buttons.length;
    });
  }

  addSection(): void {
    this.sections.push(createAnotherSection());
    this.wizardStepComponent.isSummaryDisplayedSubject
      .pipe(
        first(),
        filter((value) => value),
      )
      .subscribe(() => this.sections.at(this.sections.length - 1).markAllAsTouched());
  }

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
