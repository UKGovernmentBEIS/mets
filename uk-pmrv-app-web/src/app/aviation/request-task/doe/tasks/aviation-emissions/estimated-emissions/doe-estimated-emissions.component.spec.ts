import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { mockClass } from '@testing';

import { AviationDoECorsia, TasksService } from 'pmrv-api';

import { DoeCorsiaEmissionsFormProvider } from '../doe-corsia-emissions-form.provider';
import { EstimatedEmissionsComponent } from './doe-estimated-emissions.component';

describe('EstimatedEmissionsComponent', () => {
  let component: EstimatedEmissionsComponent;
  let fixture: ComponentFixture<EstimatedEmissionsComponent>;
  let store: RequestTaskStore;
  let formProvider: DoeCorsiaEmissionsFormProvider;

  const requestTaskFileService = mockClass(RequestTaskFileService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: TASK_FORM_PROVIDER, useClass: DoeCorsiaEmissionsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },

        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<DoeCorsiaEmissionsFormProvider>(TASK_FORM_PROVIDER);

    fixture = TestBed.createComponent(EstimatedEmissionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should call the saveDoe function with the correct data when the form is valid', async () => {
    const data = {
      ...store.doeDelegate.payload,
      determinationReason: {
        type: 'VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED',
        subtypes: null,
        furtherDetails: 'further details',
      },
      emissions: {
        emissionsAllInternationalFlights: '100',
        emissionsFlightsWithOffsettingRequirements: '10',
        emissionsClaimFromCorsiaEligibleFuels: '10',
        calculationApproach: 'text',
      },
      supportingDocuments: [],
    };

    formProvider.setFormValue({
      determinationReason: {
        type: 'VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED',
        furtherDetails: 'further details',
      },
      emissions: {
        emissionsAllInternationalFlights: '100',
        emissionsFlightsWithOffsettingRequirements: '10',
        emissionsClaimFromCorsiaEligibleFuels: '10',
        calculationApproach: 'text',
      },
    } as AviationDoECorsia);

    const saveDoeSpy = jest.spyOn(store.doeDelegate, 'saveDoe').mockReturnValue(of({}));

    component.onSubmit();

    expect(saveDoeSpy).toHaveBeenCalledWith({ doe: data }, 'in progress');

    saveDoeSpy.mockRestore();
  });
});
