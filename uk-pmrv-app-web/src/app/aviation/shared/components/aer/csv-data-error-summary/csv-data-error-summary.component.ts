import { AsyncPipe, DOCUMENT, KeyValuePipe, NgForOf, NgIf, NgTemplateOutlet } from '@angular/common';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  Input,
  OnChanges,
  ViewChild,
} from '@angular/core';
import { AbstractControl, FormControlStatus, NgForm, UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';

import { map, Observable, startWith, tap } from 'rxjs';

import { NestedMessageValidationErrorsArray } from '@aviation/shared/components/aer/csv-data-error-summary/error-detail';
import { SharedModule } from '@shared/shared.module';

import { FormService } from 'govuk-components';

@Component({
  selector: 'app-csv-data-error-summary',
  templateUrl: './csv-data-error-summary.component.html',
  styles: [],
  imports: [AsyncPipe, KeyValuePipe, NgTemplateOutlet, NgIf, NgForOf, RouterLink, SharedModule],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CsvDataErrorSummaryComponent implements OnChanges, AfterViewInit {
  @Input() form: UntypedFormGroup | NgForm;

  @ViewChild('container', { read: ElementRef }) container: ElementRef<HTMLDivElement>;

  errorList$: Observable<NestedMessageValidationErrorsArray[]>;

  private formControl: UntypedFormGroup;

  constructor(
    @Inject(DOCUMENT) private readonly document,
    private readonly formService: FormService,
    private readonly title: Title,
  ) {}

  ngOnChanges(): void {
    this.formControl = this.form instanceof UntypedFormGroup ? this.form : this.form.control;

    const statusChanges: Observable<FormControlStatus> = this.form.statusChanges;
    this.errorList$ = statusChanges.pipe(
      startWith(this.form.status),
      map((status) => (status === 'INVALID' ? this.getAbstractControlErrors(this.formControl) : null)),
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
    if (this.container?.nativeElement?.scrollIntoView) {
      this.container.nativeElement.scrollIntoView();
    }
    if (this.container?.nativeElement?.focus) {
      this.container.nativeElement.focus();
    }
  }

  private getAbstractControlErrors(
    control: AbstractControl,
    path: string[] = [],
  ): NestedMessageValidationErrorsArray[] {
    let childControlErrors = [];

    if (control instanceof UntypedFormGroup) {
      childControlErrors = Object.entries(control.controls)
        .map(([key, value]) => this.getAbstractControlErrors(value, path.concat([key])))
        .reduce((errors, controlErrors) => errors.concat(controlErrors), []);
    } else if (control instanceof UntypedFormArray) {
      childControlErrors = control.controls
        .map((arrayControlItem, index) => this.getAbstractControlErrors(arrayControlItem, path.concat([String(index)])))
        .reduce((errors, controlErrors) => errors.concat(controlErrors), []);
    }

    const errors = control.errors;

    if (errors) {
      const errorEntries = Object.keys(errors).map((key) => ({
        type: key,
        path: this.formService.getIdentifier(path),
        ...errors[key],
      }));
      return childControlErrors.concat(errorEntries);
    }

    return childControlErrors;
  }

  getRowIndexes(rows: any[]): number[] {
    return rows.map((row) => row.rowIndex);
  }
}
