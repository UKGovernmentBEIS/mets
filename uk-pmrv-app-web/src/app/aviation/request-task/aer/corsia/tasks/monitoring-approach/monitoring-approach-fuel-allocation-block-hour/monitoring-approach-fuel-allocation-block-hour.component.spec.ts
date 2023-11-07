import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaAircraftTypeDetails,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaMonitoringApproach,
  TasksService,
} from 'pmrv-api';

import { AviationAerCorsiaMonitoringApproachFormProvider } from '../monitoring-approach-form.provider';
import { MonitoringApproachFuelAllocationBlockHourComponent } from './monitoring-approach-fuel-allocation-block-hour.component';

describe('MonitoringApproachFuelAllocationBlockHourComponent', () => {
  let component: MonitoringApproachFuelAllocationBlockHourComponent;
  let fixture: ComponentFixture<MonitoringApproachFuelAllocationBlockHourComponent>;
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let formProvider: AviationAerCorsiaMonitoringApproachFormProvider;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);
  const initialState = {
    certUsed: false,
    fuelUseMonitoringDetails: {
      fuelDensityType: 'ACTUAL_DENSITY',
      identicalToProcedure: true,
      blockHourUsed: true,
    },
  } as AviationAerCorsiaMonitoringApproach;

  class Page extends BasePage<MonitoringApproachFuelAllocationBlockHourComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorTitles() {
      return this.queryAll<HTMLButtonElement>('.govuk-error-summary__list li p');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, FormsModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AviationAerCorsiaMonitoringApproachFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: ChangeDetectorRef, useValue: {} },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    const state = store.getState();
    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          type: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD',
            aer: { monitoringApproach: initialState },
            aerSectionsCompleted: {},
          } as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
        },
      },
    });

    formProvider = TestBed.inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(initialState);

    fixture = TestBed.createComponent(MonitoringApproachFuelAllocationBlockHourComponent);
    component = fixture.componentInstance;
    router = fixture.debugElement.injector.get(Router);
    fixture.detectChanges();
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display instruction to upload a .csv file', () => {
    const paragraphElements = fixture.debugElement.queryAll(By.css('.govuk-body'));
    const uploadInstruction = paragraphElements[0].nativeElement;
    expect(uploadInstruction.textContent).toContain(
      'Upload a comma-separated values (.csv) file of the average fuel burn ratio for each aeroplane type, for the reporting year.',
    );
  });

  it('should display "Choose a CSV file" button when file is not loaded', () => {
    component.fileLoaded = false;
    fixture.detectChanges();
    const chooseFileButton = fixture.debugElement.query(By.css('button'));
    expect(chooseFileButton.nativeElement.textContent).toBe('Choose a CSV file');
  });

  it('should not display any error when CSV is valid', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    expect(page.errorSummary).toBeFalsy();

    component.fileLoaded = true;
    component.processCSVData([['C560', 'test', '1.2']]);
    fixture.detectChanges();
    expect(page.errorSummary).toBeFalsy();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should display aircraft designator error', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    component.fileLoaded = true;
    component.processCSVData([['', 'test', '1.2']]);
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorTitles[0]).toHaveTextContent('Enter an aircraft designator');
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should not display aircraft sub-type error', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    component.fileLoaded = true;
    component.processCSVData([['C560', '', '1.2']]);
    fixture.detectChanges();
    expect(page.errorSummary).toBeFalsy();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should display fuel burn ratio error', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    component.fileLoaded = true;
    component.processCSVData([['C560', 'test', '']]);
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errorTitles[0]).toHaveTextContent('Enter a fuel burn ratio');

    component.processCSVData([['C560', 'test', '1.2345']]);
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errorTitles[0]).toHaveTextContent('Enter a positive number, up to 3 decimals');

    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should call the saveAer function with the correct data when the form is valid and navigate to next page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const aircraftTypeDetails: AviationAerCorsiaAircraftTypeDetails[] = [
      {
        designator: 'C560',
        subtype: 'test',
        fuelBurnRatio: '1.2',
      },
      {
        designator: 'A320',
        subtype: 'test',
        fuelBurnRatio: '1',
      },
    ];

    const monitoringApproach = {
      certUsed: false,
      fuelUseMonitoringDetails: {
        fuelDensityType: 'ACTUAL_DENSITY',
        identicalToProcedure: true,
        blockHourUsed: true,
        aircraftTypeDetails: aircraftTypeDetails,
      },
    } as AviationAerCorsiaMonitoringApproach;

    const data = {
      monitoringApproach: {
        ...monitoringApproach,
      },
    };

    component.fileLoaded = true;
    component.processCSVData(aircraftTypeDetails.map((obj) => Object.values(obj)));
    fixture.detectChanges();
    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith(data, 'in progress');
    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRouteStub });
  });
});
