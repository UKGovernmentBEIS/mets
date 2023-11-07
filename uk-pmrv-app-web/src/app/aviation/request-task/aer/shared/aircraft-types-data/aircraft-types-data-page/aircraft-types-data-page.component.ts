import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, map, take } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { AircraftTypesDataTableComponent } from '@aviation/shared/components/aer/aircraft-types-table/aircraft-types-data-table.component';
import {
  exampleColumns,
  exampleData,
} from '@aviation/shared/components/aer/aircraft-types-table/column-header-mapping';
import { CsvDataWizardStepComponent } from '@aviation/shared/components/aer/csv-data-wizard-step';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import {
  emptyFileValidator,
  fileExtensionValidator,
  fileNameLengthValidator,
  maxFileSizeValidator,
} from '@aviation/shared/validators';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import moment from 'moment';
import Papa from 'papaparse';

import { AviationAerAircraftDataDetails } from 'pmrv-api';

import { AircraftTypesDataFormProvider } from '../aircraft-types-data-form.provider';

@Component({
  selector: 'app-aircraft-types-data-page',
  templateUrl: './aircraft-types-data-page.component.html',
  styles: [],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, CsvDataWizardStepComponent, AircraftTypesDataTableComponent],
})
export class AircraftTypesDataPageComponent implements OnInit {
  form = this.formProvider.form;
  @ViewChild(CsvDataWizardStepComponent) wizardStep: CsvDataWizardStepComponent;
  parsedData: AviationAerAircraftDataDetails[];
  fileLoaded = false;
  fileName = 'File uploaded';
  alreadyUploaded = false;
  exampleTableData: any;
  exampleTableColumns = exampleColumns;
  uploadedFile: File;
  errorList = [];
  scheme$ = this.store.pipe(
    aerQuery.selectIsCorsia,
    map((isCorsia) => (isCorsia ? 'CORSIA' : 'UK ETS')),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: AircraftTypesDataFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private cd: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const aircraftTypesDataControl = this.form.get('aviationAerAircraftDataDetails');
    let aviationAerAircraftDataDetails = null;
    if (aircraftTypesDataControl && aircraftTypesDataControl.value) {
      aviationAerAircraftDataDetails = aircraftTypesDataControl.value;
    }

    if (Array.isArray(aviationAerAircraftDataDetails)) {
      this.fileLoaded = true;
      this.parsedData = aviationAerAircraftDataDetails;
      this.alreadyUploaded = true;
    }

    this.exampleTableData = exampleData;
    this.form.updateValueAndValidity();
  }

  onSubmit() {
    const payload = {
      aviationAerAircraftData: {
        aviationAerAircraftDataDetails: this.form.get('aviationAerAircraftDataDetails').value,
      },
    };

    this.store.aerDelegate
      .saveAer(payload, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setAviationAerAircraftData(this.formProvider.getFormValue());
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }

  onFileSelect(event: any) {
    this.form.get('aviationAerAircraftDataDetails').reset();
    this.wizardStep.isSummaryDisplayedSubject.next(false);
    this.uploadedFile = event.target.files[0];
    const fileControl = new FormControl(this.uploadedFile, [
      fileExtensionValidator(['csv'], ['text/csv', 'application/vnd.ms-excel'], 'Only CSV files are accepted'),
      maxFileSizeValidator(20, 'Maximum allowed file size is 20 MB'),
      fileNameLengthValidator(100, 'Maximum allowed file name length is 100 characters'),
      emptyFileValidator('Empty file uploaded'),
    ]);

    this.errorList = [];

    if (fileControl.errors) {
      this.parsedData = null;
      for (const errorKey in fileControl.errors) {
        if (Object.prototype.hasOwnProperty.call(fileControl.errors, errorKey)) {
          this.errorList.push(fileControl.errors[errorKey]);
        }
      }
    }

    if (this.errorList.length === 0) {
      Papa.parse(this.uploadedFile, {
        complete: (result) => {
          this.processCSVData(result.data);
        },
      });
      this.alreadyUploaded = false;
    }
    event.target.value = '';
  }

  processCSVData(data: any[]) {
    if (data.length > 0 && data[data.length - 1].join('').trim() === '') {
      data.pop();
    }

    let tempData = data.map((row) => {
      return {
        aircraftTypeDesignator: row[0],
        subType: row[1],
        registrationNumber: row[2],
        ownerOrLessor: row[3],
        startDate: moment(row[4], 'DD/MM/YYYY', true).isValid()
          ? moment(row[4], 'DD/MM/YYYY', true).format('YYYY-MM-DD')
          : row[4],
        endDate: moment(row[5], 'DD/MM/YYYY', true).isValid()
          ? moment(row[5], 'DD/MM/YYYY', true).format('YYYY-MM-DD')
          : row[5],
      } as AviationAerAircraftDataDetails;
    });

    return combineLatest([this.store.pipe(aerQuery.selectAer), this.store.pipe(aerQuery.selectAerYear)])
      .pipe(take(1))
      .subscribe(([aer, aerYear]) => {
        tempData = tempData.map((row) => {
          return {
            ...row,
            ownerOrLessor: row.ownerOrLessor === '' ? aer.operatorDetails.operatorName : row.ownerOrLessor,
            startDate: row.startDate === '' && aerYear ? aerYear + '-01-01' : row.startDate,
            endDate: row.endDate === '' && aerYear ? aerYear + '-12-31' : row.endDate,
          };
        });

        this.fileLoaded = true;
        this.formProvider.form.statusChanges
          .pipe(
            filter((status) => status !== 'PENDING'),
            take(1),
          )
          .subscribe(() => {
            if (!this.formProvider.getAircraftDataDetailsControl().errors) {
              this.parsedData = tempData;
            } else {
              this.wizardStep.isSummaryDisplayedSubject.next(true);
              this.parsedData = null;
            }
            this.cd.detectChanges();
          });
        this.formProvider.getAircraftDataDetailsControl().setValue(tempData);
      });
  }
}
