import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, filter, map, Observable, of, Subscription, switchMap, take } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { CsvDataWizardStepComponent } from '@aviation/shared/components/aer/csv-data-wizard-step/csv-data-wizard-step.component';
import { exampleColumns, exampleData } from '@aviation/shared/components/aer/flight-data-table/column-header-mapping';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table/flight-data-table.component';
import { mapFuelType } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';
import { AircraftTypeTableComponent } from '@aviation/shared/components/emp/emission-sources/aircraft-type/table/aircraft-type-table.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import {
  emptyFileValidator,
  fileExtensionValidator,
  fileNameLengthValidator,
  maxFileSizeValidator,
} from '@aviation/shared/validators';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';
import Papa from 'papaparse';

import {
  AviationAerCorsiaAggregatedEmissionDataDetails,
  AviationAerUkEtsAggregatedEmissionDataDetails,
  AviationReportedAirportsService,
  AviationRptAirportsDTO,
} from 'pmrv-api';

import { RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { AggregatedConsumptionFlightDataFormProvider } from '../aggregated-consumption-flight-data-form.provider';

@Component({
  selector: 'app-aggregated-consumption-flight-data-page',
  templateUrl: './aggregated-consumption-flight-data-page.component.html',
  styles: [],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    AircraftTypeTableComponent,
    FlightDataTableComponent,
    CsvDataWizardStepComponent,
  ],
})
export class AggregatedConsumptionFlightDataPageComponent implements OnInit, OnDestroy {
  form = this.formProvider.form;
  @ViewChild('fileInput') fileInput: ElementRef;
  @ViewChild(CsvDataWizardStepComponent) wizardStep: CsvDataWizardStepComponent;
  parsedData: AviationAerUkEtsAggregatedEmissionDataDetails[] | AviationAerCorsiaAggregatedEmissionDataDetails[];
  fileLoaded = false;
  fileName = 'File uploaded';
  uploadedFile: File;
  alreadyUploaded = false;
  exampleTableColumns = exampleColumns;
  exampleTableData: any;
  errorList = [];
  isCorsia$ = this.store.pipe(aerQuery.selectIsCorsia);
  private subscription: Subscription;
  private statusSubscription: Subscription;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: AggregatedConsumptionFlightDataFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private backLinkService: BackLinkService,
    private router: Router,
    private route: ActivatedRoute,
    private cd: ChangeDetectorRef,
    private aviationReportedAirportsService: AviationReportedAirportsService,
  ) {}

  fileControl: FormControl = new FormControl(
    null,
    [
      fileExtensionValidator(['csv'], ['text/csv', 'application/vnd.ms-excel'], 'The file must be in the .csv format'),
      maxFileSizeValidator(20, 'The file size must be less than 20 megabytes'),
      fileNameLengthValidator(100, 'The file name must be less than 100 characters'),
      emptyFileValidator('Empty file uploaded'),
    ],
    [
      this.csvRowValidator(
        'Each row must have 5 comma separated values, labelled ‘aerodrome of departure’, ' +
          '‘aerodrome of arrival’, ‘fuel used’, ‘fuel consumption’, ‘number of flights',
      ),
      this.noHeaderValidator('The file must not contain a header row'),
    ],
  );

  ngOnInit(): void {
    this.backLinkService.show();

    const aggregatedEmissionDataControl = this.form.get('aggregatedEmissionDataDetails');
    let aggregatedEmissionDataDetails = null;
    if (aggregatedEmissionDataControl && aggregatedEmissionDataControl.value) {
      aggregatedEmissionDataDetails = aggregatedEmissionDataControl.value;
    }

    if (Array.isArray(aggregatedEmissionDataDetails)) {
      this.fileLoaded = true;
      this.parsedData = aggregatedEmissionDataDetails;
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
      aggregatedEmissionsData: {
        aggregatedEmissionDataDetails: this.form.get('aggregatedEmissionDataDetails').value,
      },
    };

    this.store.aerDelegate
      .saveAer(payload, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setAggregatedEmissionsData(this.formProvider.getFormValue());
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.statusSubscription) {
      this.statusSubscription.unsubscribe();
    }
    this.backLinkService.hide();
  }

  onFileSelect(event: any) {
    this.form.get('aggregatedEmissionDataDetails').reset();
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
    const icaos = data.reduce((acc, row) => [...acc, row[0], row[1]], []);

    this.subscription = this.store
      .pipe(aerQuery.selectAerYear)
      .pipe(
        take(1),
        switchMap((year) =>
          this.isCorsia$.pipe(
            take(1),
            switchMap((isCorsia) =>
              this.aviationReportedAirportsService.getReportedAirports({ icaos: icaos, year: year }).pipe(
                catchError((error) => {
                  return of({ error });
                }),
                map((reportedAirports: AviationRptAirportsDTO[]) => {
                  return data.map((row) => {
                    const icaoFrom = row[0];
                    const icaoTo = row[1];

                    const airportFrom = reportedAirports.find((airport) => airport.icao === icaoFrom);
                    const airportTo = reportedAirports.find((airport) => airport.icao === icaoTo);

                    const mappedData = {
                      airportFrom: {
                        icao: airportFrom?.icao || icaoFrom,
                        name: airportFrom?.name,
                        country: airportFrom?.country,
                        countryType: airportFrom?.countryType,
                        state: airportFrom?.state,
                      },
                      airportTo: {
                        icao: airportTo?.icao || icaoTo,
                        name: airportTo?.name,
                        country: airportTo?.country,
                        countryType: airportTo?.countryType,
                        state: airportTo?.state,
                      },
                      fuelConsumption: row[3],
                      flightsNumber: +row[4],
                    };

                    return isCorsia
                      ? ({
                          ...mappedData,
                          fuelType: mapFuelType(row[2], true),
                        } as AviationAerCorsiaAggregatedEmissionDataDetails)
                      : ({
                          ...mappedData,
                          fuelType: mapFuelType(row[2]),
                        } as AviationAerUkEtsAggregatedEmissionDataDetails);
                  });
                }),
              ),
            ),
          ),
        ),
      )
      .subscribe((tempData) => {
        this.fileLoaded = true;
        this.form.get('aggregatedEmissionDataDetails').setValue(tempData);
        this.form.get('aggregatedEmissionDataDetails').updateValueAndValidity();

        setTimeout(() => {
          if (!this.form.get('aggregatedEmissionDataDetails').errors) {
            this.parsedData = tempData;
          } else {
            this.wizardStep.isSummaryDisplayedSubject.next(true);
            this.parsedData = null;
          }
          this.cd.detectChanges();
        }, 500);
      });
  }

  csvRowValidator(message: string): AsyncValidatorFn {
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

              if (values.length !== 5) {
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

  noHeaderValidator(message: string): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      return new Observable((observer) => {
        const file = control.value;

        if (file instanceof File) {
          const reader = new FileReader();

          reader.onload = (e: any) => {
            const fileContent = e.target.result;
            const firstRow = fileContent.split('\n')[0];
            const values = firstRow.split(',');

            if (!/^[A-Z]{4}$/.test(values[0]) || !/^[A-Z]{4}$/.test(values[1])) {
              observer.next({ headerDetected: { message } });
              observer.complete();
              return;
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

  private processControlStatus(): void {
    this.errorList = [];
    if (this.fileControl.errors) {
      this.parsedData = null;
      for (const errorKey in this.fileControl.errors) {
        if (Object.prototype.hasOwnProperty.call(this.fileControl.errors, errorKey)) {
          this.errorList.push(this.fileControl.errors[errorKey]);
        }
      }
      this.cd.markForCheck();
    }
    if (this.errorList.length === 0 && this.uploadedFile) {
      Papa.parse(this.uploadedFile, {
        complete: (result) => {
          this.processCSVData(result.data);
        },
      });
      this.alreadyUploaded = false;
    }
  }
}
