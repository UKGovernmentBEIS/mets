import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { DoeCorsiaEmissionsFormProvider } from '../doe-corsia-emissions-form.provider';
import { EmissionsChargesComponent } from './emission-charges.component';

describe('EmissionsChargesComponent', () => {
  let component: EmissionsChargesComponent;
  let fixture: ComponentFixture<EmissionsChargesComponent>;
  let store: RequestTaskStore;
  let formProvider: DoeCorsiaEmissionsFormProvider;

  const requestTaskFileService = mockClass(RequestTaskFileService);
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: TASK_FORM_PROVIDER, useClass: DoeCorsiaEmissionsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<DoeCorsiaEmissionsFormProvider>(TASK_FORM_PROVIDER);
    fixture = TestBed.createComponent(EmissionsChargesComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should call the saveDoe function with the correct data when the form is valid', async () => {
    const data = {
      ...store.doeDelegate.payload,
      determinationReason: {
        type: 'VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED',
        furtherDetails: 'further details',
        subtypes: null,
      },
      emissions: {
        calculationApproach: 'text',
        emissionsAllInternationalFlights: '100',
        emissionsClaimFromCorsiaEligibleFuels: '10',
        emissionsFlightsWithOffsettingRequirements: '10',
      },
      fee: {
        chargeOperator: true,
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
      fee: {
        chargeOperator: true,
      },
    } as any);

    const saveDoeSpy = jest.spyOn(store.doeDelegate, 'saveDoe').mockReturnValue(of({}));

    component.onSubmit();

    expect(saveDoeSpy).toHaveBeenCalledWith({ doe: data }, 'in progress');

    saveDoeSpy.mockRestore();
  });
});
