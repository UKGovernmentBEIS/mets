import { AsyncPipe, KeyValuePipe } from '@angular/common';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  DOCUMENT,
  ElementRef,
  inject,
  input,
  OnChanges,
  viewChild,
  viewChildren,
} from '@angular/core';
import { AbstractControl, FormControlStatus, NgForm, UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';

import { map, Observable, startWith, tap } from 'rxjs';

import { FormService } from '../form/form.service';
import { FlatSummaryError, NestedMessageValidationErrors } from './nested-message-validation-errors';

@Component({
  selector: 'govuk-error-summary',
  imports: [RouterLink, KeyValuePipe, AsyncPipe],
  templateUrl: './error-summary.component.html',
  styleUrl: './error-summary.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorSummaryComponent implements OnChanges, AfterViewInit {
  private readonly document = inject<Document>(DOCUMENT);
  private readonly formService = inject(FormService);
  private readonly title = inject(Title);

  readonly form = input<UntypedFormGroup | NgForm>();

  readonly inputs = viewChildren('anchor', { read: ElementRef });
  readonly container = viewChild('container', { read: ElementRef });

  errorList$: Observable<FlatSummaryError[]>;

  private formGroup: UntypedFormGroup;

  ngOnChanges(): void {
    const form = this.form();
    this.formGroup = form instanceof UntypedFormGroup ? form : form.control;

    const statusChanges: Observable<FormControlStatus> = this.form().statusChanges;
    this.errorList$ = statusChanges.pipe(
      startWith(this.form().status),
      map((status) => status === 'INVALID' && this.getAbstractControlErrors(this.formGroup)),
      map((errors) => this.flattenErrors(errors).sort(this.sortByPosition)),
      map((errors) => (errors.length ? errors : null)),
      tap((errors) => {
        const currentTitle = this.title.getTitle();
        const prefix = 'Error: ';

        if (errors && !currentTitle.startsWith(prefix)) {
          this.title.setTitle(prefix.concat(currentTitle));
        } else if (!errors) {
          this.title.setTitle(currentTitle.replace(prefix, ''));
        }
      }),
    );
  }

  ngAfterViewInit(): void {
    const container = this.container();
    if (container?.nativeElement?.scrollIntoView) {
      container.nativeElement.scrollIntoView();
    }
    if (container?.nativeElement?.focus) {
      container.nativeElement.focus();
    }
  }

  errorClick(path: string): void {
    if (!path) {
      return;
    }
    const labelOrLegend = this.document.getElementById(`l.${path}`);
    if (labelOrLegend) {
      labelOrLegend.scrollIntoView();
    }

    // Case radio - first option
    let targetInput: HTMLElement = this.document.getElementById(`${path}-option0`);
    if (!targetInput) {
      // Case checkbox - first checkbox
      targetInput = this.document.getElementById(`${path}-0`);
    }
    if (!targetInput) {
      // Case date - first input with error
      targetInput = this.document.getElementById(`${path}-day`);
      if (targetInput && !targetInput.classList.contains('govuk-input--error')) {
        targetInput = this.document.getElementById(`${path}-month`);
        if (targetInput && !targetInput.classList.contains('govuk-input--error')) {
          targetInput = this.document.getElementById(`${path}-year`);
          if (targetInput && !targetInput.classList.contains('govuk-input--error')) {
            targetInput = this.document.getElementById(`${path}-day`);
          }
        }
      }
    }
    // Case text, textarea, select
    if (!targetInput) {
      targetInput = this.document.getElementById(path);
    }
    if (targetInput) {
      targetInput.focus({ preventScroll: true });
    }
  }

  private flattenErrors(errors: NestedMessageValidationErrors, flattenedErrors = []): FlatSummaryError[] {
    if (errors.self) {
      flattenedErrors.push({ path: errors.path, self: errors.self });
    }
    if (errors.controls) {
      Object.values(errors.controls).forEach((value) => this.flattenErrors(value, flattenedErrors));
    }
    return flattenedErrors;
  }

  private sortByPosition = (a: FlatSummaryError, b: FlatSummaryError) => {
    const combinedSelector = [a.path, b.path]
      .filter((selector) => !!selector)
      .map((selector) => `#${this.sanitizeSelector(selector)}`)
      .join(', ');
    const elements: HTMLElement[] = Array.from(this.document.querySelectorAll(combinedSelector));
    const aIndex = elements.findIndex((element) => element.id === a.path);
    const bIndex = elements.findIndex((element) => element.id === b.path);

    return aIndex === -1 ? 1 : bIndex === -1 ? -1 : aIndex - bIndex;
  };

  private getAbstractControlErrors(control: AbstractControl, path: string[] = []): NestedMessageValidationErrors {
    let childControlErrors;

    if (control instanceof UntypedFormGroup) {
      childControlErrors = Object.entries(control.controls)
        .map(([key, value]) => ({ [key]: this.getAbstractControlErrors(value, path.concat([key])) }))
        .reduce((errors, controlErrors) => ({ ...errors, ...controlErrors }), {});
    } else if (control instanceof UntypedFormArray) {
      childControlErrors = control.controls.map((arrayControlItem, index) =>
        this.getAbstractControlErrors(arrayControlItem, path.concat([String(index)])),
      );
    }

    return {
      path: this.formService.getIdentifier(path),
      self: control.errors,
      controls: childControlErrors,
    };
  }

  private sanitizeSelector(selector: string): string {
    return selector.replace(/\./g, '\\.');
  }
}
