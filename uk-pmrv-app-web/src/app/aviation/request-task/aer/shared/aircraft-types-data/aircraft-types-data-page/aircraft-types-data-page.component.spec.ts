import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { BasePage, mockClass } from '@testing';

import { AircraftTypesService } from 'pmrv-api';

import { AircraftTypesDataFormProvider } from '../aircraft-types-data-form.provider';
import { AircraftTypesDataPageComponent } from './aircraft-types-data-page.component';

describe('AircraftTypesDataPageComponent', () => {
  let component: AircraftTypesDataPageComponent;
  let fixture: ComponentFixture<AircraftTypesDataPageComponent>;
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;

  const aircraftTypesService = mockClass(AircraftTypesService);

  class Page extends BasePage<AircraftTypesDataPageComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorTitles() {
      return this.queryAll<HTMLButtonElement>('.govuk-error-summary__list li p');
    }
  }

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, FormsModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AircraftTypesDataFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
        { provide: BackLinkService, useValue: mockClass(BackLinkService) },
        { provide: AircraftTypesService, useValue: aircraftTypesService },
        { provide: ChangeDetectorRef, useValue: {} },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    aircraftTypesService.getAircraftTypes.mockReturnValue(of({ aircraftTypes: [] }));

    const state = store.getState();
    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestInfo: { type: 'AVIATION_AER_UKETS' },
        requestTask: {
          type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD',
            reportingYear: '2022',
          },
        },
      },
    } as any);

    fixture = TestBed.createComponent(AircraftTypesDataPageComponent);
    component = fixture.componentInstance;
    router = fixture.debugElement.injector.get(Router);
    fixture.detectChanges();
    page = new Page(fixture);
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display instruction to upload a .csv file', () => {
    const paragraphElements = fixture.debugElement.queryAll(By.css('.govuk-body'));
    const uploadInstruction = paragraphElements[0].nativeElement;
    expect(uploadInstruction.textContent).toContain(
      'Upload a comma-separated values (.csv) file of the aircraft types which you have operated during the scheme year on UK ETS flights. This should include owned and leased-in aircraft.',
    );
  });

  it('should display "Choose a CSV file" button when file is not loaded', () => {
    component.fileLoaded = false;
    fixture.detectChanges();
    const chooseFileButton = fixture.debugElement.query(By.css('button'));
    expect(chooseFileButton.nativeElement.textContent).toBe('Choose a CSV file');
  });

  it('should display start after end date error', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    component.fileLoaded = true;
    component.processCSVData([['C560', '', 'D-CAPB', 'Aviation Operator', '05/01/2022', '04/01/2022']]);
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorTitles[0]).toHaveTextContent('The start date must be the same as or before the end date');
      expect(navigateSpy).not.toHaveBeenCalled();
    });
  });

  it('should display startDate error', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    component.fileLoaded = true;
    component.processCSVData([['C560', '', 'D-CAPB', 'Aviation Operator', '2022/05/01', '04/01/2022']]);
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorTitles[0]).toHaveTextContent('The date must be entered as dd/mm/yyyy');
      expect(navigateSpy).not.toHaveBeenCalled();
    });
  });

  it('should display mandatory designator error', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    component.fileLoaded = true;
    component.processCSVData([['', '', 'D-CAPB', 'Aviation Operator', '01/01/2022', '04/01/2022']]);
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorTitles[0]).toHaveTextContent('Enter an aircraft designator');
      expect(navigateSpy).not.toHaveBeenCalled();
    });
  });

  it('should display mandatory registration number error', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    component.fileLoaded = true;
    component.processCSVData([['C560', '', '', 'Aviation Operator', '01/01/2022', '04/01/2022']]);
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorTitles[0]).toHaveTextContent('Enter an aircraft registration number');
      expect(navigateSpy).not.toHaveBeenCalled();
    });
  });
});
