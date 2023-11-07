import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, startWith, take } from 'rxjs';

import {
  AircraftTypeDetailsFormArray,
  AviationAerCorsiaMonitoringApproachFormProvider,
} from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { designatorValidator } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/validators';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { AircraftFuelBurnRatioTableComponent } from '@aviation/shared/components/aer/aircraft-fuel-burn-ratio-table/aircraft-fuel-burn-ratio-table.component';
import {
  columnHeaderTitles,
  rowExamples,
} from '@aviation/shared/components/aer/aircraft-fuel-burn-ratio-table/column-header-mapping';
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
import Papa from 'papaparse';

import {
  AircraftTypesService,
  AviationAerCorsiaAircraftTypeDetails,
  AviationAerCorsiaMonitoringApproach,
} from 'pmrv-api';

@Component({
  selector: 'app-aircraft-types-data-page',
  templateUrl: './monitoring-approach-fuel-allocation-block-hour.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, CsvDataWizardStepComponent, AircraftFuelBurnRatioTableComponent],
})
export class MonitoringApproachFuelAllocationBlockHourComponent implements OnInit {
  @ViewChild(CsvDataWizardStepComponent) wizardStep: CsvDataWizardStepComponent;

  private formProvider = inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cd = inject(ChangeDetectorRef);
  private aircraftTypesService = inject(AircraftTypesService);
  form = this.formProvider.getFormAsGroup('aircraftTypeDetails') as FormGroup<{
    aircraftTypeDetails: AircraftTypeDetailsFormArray;
  }>;
  aircraftTypeDetailsCtrl = this.form.controls.aircraftTypeDetails;

  fileLoaded = false;
  fileName = 'File uploaded';
  alreadyUploaded = false;
  exampleTableData = rowExamples;
  exampleTableColumns = columnHeaderTitles;
  errorList = [];
  parsedData?: AviationAerCorsiaAircraftTypeDetails[];
  uploadedFile: File;

  ngOnInit(): void {
    this.aircraftTypeDetailsCtrl.addAsyncValidators(
      designatorValidator(
        'The designator entered is not valid. Please check and try again.',
        this.aircraftTypesService,
      ),
    );
    this.aircraftTypeDetailsCtrl.updateValueAndValidity();
    if (this.aircraftTypeDetailsCtrl.value?.length > 0) {
      this.fileLoaded = true;
      this.alreadyUploaded = true;
      this.parsedData = this.aircraftTypeDetailsCtrl.value as AviationAerCorsiaAircraftTypeDetails[];
    }
  }

  onFileSelect(event: any) {
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

    const processedData = data.map((row) => {
      return {
        designator: row[0],
        subtype: row[1],
        fuelBurnRatio: row[2],
      } as AviationAerCorsiaAircraftTypeDetails;
    });

    this.aircraftTypeDetailsCtrl.clear();
    processedData?.map((aircraftDetail) =>
      this.aircraftTypeDetailsCtrl.push(this.formProvider.addAircraftTypeDetailsGroup(aircraftDetail)),
    );

    this.fileLoaded = true;
    this.form.statusChanges
      .pipe(
        startWith(this.form.status),
        filter((status) => status !== 'PENDING'),
        take(1),
      )
      .subscribe(() => {
        if (!this.aircraftTypeDetailsCtrl.errors) {
          this.parsedData = processedData;
        } else {
          this.wizardStep.isSummaryDisplayedSubject.next(true);
          this.parsedData = null;
        }
        this.form.markAsDirty();
        this.cd.detectChanges();
      });
  }
  onSubmit() {
    this.aircraftTypeDetailsCtrl.clearAsyncValidators();
    this.aircraftTypeDetailsCtrl.updateValueAndValidity();
    const nextRoute = '../summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.store.aerDelegate as AerCorsiaStoreDelegate)
        .saveAer({ monitoringApproach: this.getFormData() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate([nextRoute], { relativeTo: this.route }).then();
        });
    }
  }

  private getFormData(): AviationAerCorsiaMonitoringApproach {
    return {
      ...this.formProvider.form.value,
      fuelUseMonitoringDetails: {
        ...this.formProvider.form.controls.fuelUseMonitoringDetails.value,
        aircraftTypeDetails: this.aircraftTypeDetailsCtrl.value,
      },
    } as AviationAerCorsiaMonitoringApproach;
  }
}
