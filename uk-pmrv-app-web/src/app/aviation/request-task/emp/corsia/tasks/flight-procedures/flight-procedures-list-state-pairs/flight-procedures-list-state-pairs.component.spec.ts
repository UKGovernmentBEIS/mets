import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';

import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';
import FlightProceduresListStatePairsComponent from './flight-procedures-list-state-pairs.component';

describe('FlightProceduresListStatePairsComponent', () => {
  let fixture: ComponentFixture<FlightProceduresListStatePairsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlightProceduresListStatePairsComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: FlightProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
        { provide: ChangeDetectorRef, useValue: {} },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FlightProceduresListStatePairsComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display instruction to upload a .csv file', () => {
    const paragraphElements = fixture.debugElement.queryAll(By.css('.govuk-body'));
    const uploadInstruction = paragraphElements[0].nativeElement;

    expect(uploadInstruction.textContent).toContain(
      'Upload a comma-separated values (.csv) file containing a list of the state pairs for international flights that you operate.',
    );
  });

  it('should display "Choose a CSV file" button when file is not loaded', () => {
    fixture.componentInstance.fileLoaded = false;
    fixture.detectChanges();

    const chooseFileButton = fixture.debugElement.query(By.css('button'));

    expect(chooseFileButton.nativeElement.textContent).toBe('Choose a CSV file');
  });
});
