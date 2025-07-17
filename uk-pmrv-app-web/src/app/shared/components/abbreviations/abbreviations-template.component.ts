import {
  AfterContentChecked,
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';

import { filter, first, startWith, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { createAbbreviationDefinition } from '@shared/components/abbreviations/abbreviation-definition-form';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

@Component({
  selector: 'app-abbreviations-template',
  templateUrl: './abbreviations-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AbbreviationsTemplateComponent implements OnInit, AfterViewInit, AfterContentChecked {
  @Input() submitText = 'Confirm and complete';
  @Input() form: UntypedFormGroup;
  @Input() isEditable: boolean;
  @Input() caption: string;

  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  private abbreviationsLength = this.abbreviationDefs?.length ?? 0;

  constructor(
    private readonly destroy$: DestroySubject,
    private cd: ChangeDetectorRef,
  ) {}

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  get abbreviationDefs(): UntypedFormArray {
    return this.form?.get('abbreviationDefinitions') as UntypedFormArray;
  }

  ngOnInit(): void {
    this.form
      ?.get('exist')
      .valueChanges.pipe(takeUntil(this.destroy$), startWith(this.form.value.exist))
      .subscribe((exists) => {
        if (exists && this.abbreviationDefs.length === 0) {
          this.addAbbreviationDef();
        }

        if (exists) {
          this.abbreviationDefs.enable({ emitEvent: false });
        } else {
          this.abbreviationDefs.disable({ emitEvent: false });
        }
      });
  }

  ngAfterContentChecked(): void {
    this.cd.detectChanges();
  }

  ngAfterViewInit(): void {
    this.removeButtons.changes.pipe(takeUntil(this.destroy$)).subscribe((buttons) => {
      if (buttons.length > this.abbreviationsLength) {
        buttons.last.nativeElement.focus();
      }
      this.abbreviationsLength = buttons.length;
    });
  }

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }

  addAbbreviationDef(): void {
    this.abbreviationDefs.push(createAbbreviationDefinition());
    this.wizardStepComponent.isSummaryDisplayedSubject
      .pipe(
        first(),
        filter((value) => value),
      )
      .subscribe(() => this.abbreviationDefs.at(this.abbreviationDefs.length - 1).markAllAsTouched());
  }
}
