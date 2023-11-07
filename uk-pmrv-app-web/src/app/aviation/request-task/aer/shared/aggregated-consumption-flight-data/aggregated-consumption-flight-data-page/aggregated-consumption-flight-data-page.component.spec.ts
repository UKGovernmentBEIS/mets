import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { mockClass } from '@testing';

import { AviationAerCorsiaApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AggregatedConsumptionFlightDataFormProvider } from '../aggregated-consumption-flight-data-form.provider';
import { AggregatedConsumptionFlightDataPageComponent } from './aggregated-consumption-flight-data-page.component';

describe('AggregatedConsumptionFlightDataPageComponent', () => {
  let component: AggregatedConsumptionFlightDataPageComponent;
  let fixture: ComponentFixture<AggregatedConsumptionFlightDataPageComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, FormsModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AggregatedConsumptionFlightDataFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
        { provide: BackLinkService, useValue: mockClass(BackLinkService) },
        { provide: ChangeDetectorRef, useValue: {} },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
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
            aer: AerCorsiaStoreDelegate.INITIAL_STATE,
            aerSectionsCompleted: {},
          } as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(AggregatedConsumptionFlightDataPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display instruction to upload a .csv file', () => {
    const paragraphElements = fixture.debugElement.queryAll(By.css('.govuk-body'));
    const uploadInstruction = paragraphElements[0].nativeElement;
    expect(uploadInstruction.textContent).toContain('Upload a comma-separated values (.csv) file of fuel consumption');
  });

  it('should display "Choose a CSV file" button when file is not loaded', () => {
    component.fileLoaded = false;
    fixture.detectChanges();
    const chooseFileButton = fixture.debugElement.query(By.css('button'));
    expect(chooseFileButton.nativeElement.textContent).toBe('Choose a CSV file');
  });
});
