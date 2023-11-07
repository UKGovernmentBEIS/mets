import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, Observable, Subscription } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { CsvDataWizardStepComponent } from '@aviation/shared/components/aer/csv-data-wizard-step';
import { FlightProceduresDataTableComponent } from '@aviation/shared/components/emp/flight-procedures-data-table';
import {
  exampleColumns,
  exampleData,
} from '@aviation/shared/components/emp/flight-procedures-data-table/column-header-mapping';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import Papa from 'papaparse';

import { EmpOperatingStatePairsCorsiaDetails } from 'pmrv-api';

import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';

@Component({
  selector: 'app-flight-procedures-list-state-pairs',
  templateUrl: './flight-procedures-list-state-pairs.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, FlightProceduresDataTableComponent, CsvDataWizardStepComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FlightProceduresListStatePairsComponent implements OnInit, OnDestroy {
  @ViewChild(CsvDataWizardStepComponent) wizardStep: CsvDataWizardStepComponent;

  protected form = this.formProvider.operatingStatePairsCtrl;

  protected parsedData: EmpOperatingStatePairsCorsiaDetails[];
  protected fileName = 'File uploaded';
  protected uploadedFile: File;
  protected alreadyUploaded = false;
  protected exampleTableColumns = exampleColumns;
  protected exampleTableData: any;
  protected errorList = [];

  private subscription: Subscription;

  private fileControl: FormControl = new FormControl(
    null,
    [
      this._fileExtensionValidator(
        ['csv'],
        ['text/csv', 'application/vnd.ms-excel'],
        'The file must be in the .csv format',
      ),
      this._maxFileSizeValidator(20, 'The file size must be less than 20 megabytes'),
      this._fileNameLengthValidator(100, 'The file name must be less than 100 characters'),
      this._emptyFileValidator('Empty file uploaded'),
    ],
    [this._csvRowValidator('Each row must have 2 comma-separated values')],
  );

  fileLoaded = false;

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: FlightProceduresFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private cd: ChangeDetectorRef,
    private store: RequestTaskStore,
  ) {}

  ngOnInit(): void {
    const operatingStatePairsDataControl = this.form.get('operatingStatePairsCorsiaDetails');

    let operatingStatePairsCorsiaDetails = null;

    if (operatingStatePairsDataControl && operatingStatePairsDataControl.value) {
      operatingStatePairsCorsiaDetails = operatingStatePairsDataControl.value;
    }

    if (operatingStatePairsCorsiaDetails?.length > 0) {
      this.fileLoaded = true;
      this.parsedData = operatingStatePairsCorsiaDetails;
      this.alreadyUploaded = true;
    }

    this.exampleTableData = exampleData;
    this.form.updateValueAndValidity();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  onFileSelect(event: any) {
    this.form.get('operatingStatePairsCorsiaDetails').reset();
    this.wizardStep.isSummaryDisplayedSubject.next(false);
    this.uploadedFile = event.target.files[0];

    this.fileControl.setValue(this.uploadedFile);
    this.errorList = [];

    this.fileControl.updateValueAndValidity();

    this.fileControl.statusChanges
      .pipe(
        filter((status) => ['VALID', 'INVALID'].includes(status)),
        first(),
      )
      .subscribe(() => {
        if (this.fileControl.errors) {
          this.parsedData = null;

          for (const errorKey in this.fileControl.errors) {
            if (Object.prototype.hasOwnProperty.call(this.fileControl.errors, errorKey)) {
              this.errorList.push(this.fileControl.errors[errorKey]);
            }
          }
        }

        this.cd.detectChanges();

        if (this.errorList.length === 0) {
          Papa.parse(this.uploadedFile, {
            complete: (result) => {
              this._processCSVData(result.data);
            },
          });

          this.alreadyUploaded = false;
        }

        event.target.value = '';
      });
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.store.empCorsiaDelegate
      .saveEmp({ flightAndAircraftProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empCorsiaDelegate.setFlightProcedures(this.formProvider.getFormValue());
        this.router.navigate(['..', 'determination-international-flights'], { relativeTo: this.route });
      });
  }

  private _processCSVData(csvData: any[]) {
    if (csvData.length > 0 && csvData[csvData.length - 1].join('').trim() === '') {
      csvData.pop();
    }

    const data = csvData.map((row) => {
      return {
        stateA: row[0],
        stateB: row[1],
      } as EmpOperatingStatePairsCorsiaDetails;
    });

    this.fileLoaded = true;
    this.form.get('operatingStatePairsCorsiaDetails').setValue(data);
    this.form.get('operatingStatePairsCorsiaDetails').updateValueAndValidity();

    setTimeout(() => {
      if (!this.form.get('operatingStatePairsCorsiaDetails').errors) {
        this.parsedData = data;
      } else {
        this.wizardStep.isSummaryDisplayedSubject.next(true);
        this.parsedData = null;
      }

      this.cd.detectChanges();
    }, 500);
  }

  private _fileExtensionValidator(allowedExtensions: string[], allowedMimeTypes: string[], message: string) {
    return (control: FormControl): { [key: string]: any } | null => {
      const file = control.value;

      if (file) {
        const extension = file.name.split('.').pop().toLowerCase();
        const mimeType = file.type;

        if (!allowedExtensions.includes(extension) || !allowedMimeTypes.includes(mimeType)) {
          return { extensionNotAllowed: { message } };
        }
      }

      return null;
    };
  }

  private _maxFileSizeValidator(maxSizeInMB: number, message: string) {
    const maxSizeInBytes = maxSizeInMB * 1024 * 1024;

    return (control: FormControl): { [key: string]: any } | null => {
      const file = control.value;

      if (file && file.size > maxSizeInBytes) {
        return { fileTooLarge: { message } };
      }

      return null;
    };
  }

  private _fileNameLengthValidator(maxLength: number, message: string) {
    return (control: FormControl): { [key: string]: any } | null => {
      const file = control.value;

      if (file instanceof File) {
        const fileName = file.name;

        if (fileName.length > maxLength) {
          return { fileNameTooLong: { message } };
        }
      }

      return null;
    };
  }

  private _emptyFileValidator(message: string) {
    return (control: FormControl): { [key: string]: any } | null => {
      const file = control.value;

      if (file && file.size === 0) {
        return { fileEmpty: { message } };
      }

      return null;
    };
  }

  private _csvRowValidator(message: string): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      return new Observable((observer) => {
        const file = control.value;

        if (file instanceof File) {
          const reader = new FileReader();

          reader.onload = (e: any) => {
            const fileContent = e.target.result;
            const rows = fileContent.split('\n');

            if (rows[rows.length - 1].trim() === '') {
              rows.pop();
            }

            for (const row of rows) {
              const values = row.split(',');

              if (values.length !== 2) {
                observer.next({ invalidRowFormat: { message } });
                observer.complete();

                return;
              }
            }

            observer.next(null);
            observer.complete();
          };

          reader.onerror = () => {
            observer.next({ readError: { message: 'Error reading the file.' } });
            observer.complete();
          };

          reader.readAsText(file);
        } else {
          observer.next(null);
          observer.complete();
        }
      });
    };
  }
}
