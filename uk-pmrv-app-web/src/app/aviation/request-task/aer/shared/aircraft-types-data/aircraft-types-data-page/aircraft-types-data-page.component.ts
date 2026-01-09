import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Inject,
  OnDestroy,
  OnInit,
  signal,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, map, Subscription, take } from 'rxjs';

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
import { csvRowValidator } from '@shared/utils/validators';
import { format, isValid, parse } from 'date-fns';
import Papa from 'papaparse';

import { AviationAerAircraftDataDetails } from 'pmrv-api';

import { AircraftTypesDataFormProvider } from '../aircraft-types-data-form.provider';

@Component({
  selector: 'app-aircraft-types-data-page',
  templateUrl: './aircraft-types-data-page.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, CsvDataWizardStepComponent, AircraftTypesDataTableComponent],
})
export class AircraftTypesDataPageComponent implements OnInit, OnDestroy {
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
  showNotification = signal(false);
  aviationAerAircraftDataDetails =
    this.store.aerDelegate.payload.aer?.aviationAerAircraftData?.aviationAerAircraftDataDetails ||
    ([] as Array<AviationAerAircraftDataDetails>);

  private subscription: Subscription;
  private statusSubscription: Subscription;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: AircraftTypesDataFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private cd: ChangeDetectorRef,
  ) {}

  fileControl = new FormControl(
    null,
    [
      fileExtensionValidator(['csv'], ['text/csv', 'application/vnd.ms-excel'], 'Only CSV files are accepted'),
      maxFileSizeValidator(20, 'Maximum allowed file size is 20 MB'),
      fileNameLengthValidator(100, 'Maximum allowed file name length is 100 characters'),
      emptyFileValidator('Empty file uploaded'),
    ],
    [
      csvRowValidator(
        `Each row must have 6 comma separated values, labelled ‘aircraft type designator’, ‘sub type’, ‘registration number’, ‘owner or lessor name’, ‘start date’, ‘end date’`,
        6,
      ),
    ],
  );

  ngOnInit(): void {
    const aircraftTypesDataControl = this.form.get('aviationAerAircraftDataDetails');
    let aviationAerAircraftDataDetails = null;
    if (aircraftTypesDataControl?.value) {
      aviationAerAircraftDataDetails = aircraftTypesDataControl.value;
    }

    if (Array.isArray(aviationAerAircraftDataDetails)) {
      this.fileLoaded = true;
      this.parsedData = aviationAerAircraftDataDetails;
      this.alreadyUploaded = true;
    }

    this.exampleTableData = exampleData;
    this.form.updateValueAndValidity();

    this.statusSubscription = this.fileControl.statusChanges
      .pipe(filter((status: string) => status === 'INVALID' || status === 'VALID'))
      .subscribe(() => {
        this.processControlStatus();
      });
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
    this.fileControl.setValue(this.uploadedFile);
    this.fileControl.updateValueAndValidity({ emitEvent: true, onlySelf: false });

    event.target.value = '';
  }

  processCSVData(data: any[]) {
    if (data.length > 0 && data[data.length - 1].join('').trim() === '') {
      data.pop();
    }

    let tempData = data.map((row) => {
      const parsedStartDate = parse(row[4].replaceAll('-', '/'), 'dd/MM/yyyy', new Date());
      const parsedEndDate = parse(row[5].replaceAll('-', '/'), 'dd/MM/yyyy', new Date());

      return {
        aircraftTypeDesignator: row[0],
        subType: row[1],
        registrationNumber: row[2],
        ownerOrLessor: row[3],
        startDate: isValid(parsedStartDate) ? format(parsedStartDate, 'yyyy-MM-dd') : row[4].replaceAll('-', '/'),
        endDate: isValid(parsedEndDate) ? format(parsedEndDate, 'yyyy-MM-dd') : row[5].replaceAll('-', '/'),
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
              this.showNotification.set(true);
            } else {
              this.wizardStep.isSummaryDisplayedSubject.next(true);
              this.parsedData = null;
              this.showNotification.set(false);
            }
            this.cd.detectChanges();
          });
        this.formProvider.getAircraftDataDetailsControl().setValue(tempData);
        this.formProvider.getAircraftDataDetailsControl().updateValueAndValidity();
      });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.statusSubscription) {
      this.statusSubscription.unsubscribe();
    }
  }

  private processControlStatus(): void {
    this.errorList = [];
    if (this.fileControl.errors) {
      this.parsedData = null;
      for (const errorKey in this.fileControl.errors) {
        if (Object.hasOwn(this.fileControl.errors, errorKey)) {
          this.errorList.push(this.fileControl.errors[errorKey]);
          this.showNotification.set(false);
        }
      }
      this.cd.markForCheck();
    }
    if (this.errorList.length === 0 && this.uploadedFile) {
      Papa.parse(this.uploadedFile, {
        skipEmptyLines: true,
        complete: (result) => {
          this.processCSVData(result.data);
        },
      });
      this.alreadyUploaded = false;
    }
  }
}
