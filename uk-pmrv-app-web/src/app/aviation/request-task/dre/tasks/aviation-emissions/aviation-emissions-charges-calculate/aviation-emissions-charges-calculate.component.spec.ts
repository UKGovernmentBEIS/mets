import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AviationEmissionsFormProvider } from '../aviation-emissions-form.provider';
import { AviationEmissionsChargesCalculateComponent } from './aviation-emissions-charges-calculate.component';

describe('AviationEmissionsChargesCalculateComponent', () => {
  let component: AviationEmissionsChargesCalculateComponent;
  let fixture: ComponentFixture<AviationEmissionsChargesCalculateComponent>;
  let store: RequestTaskStore;
  let formProvider: AviationEmissionsFormProvider;
  const requestTaskFileService = mockClass(RequestTaskFileService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AviationEmissionsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<AviationEmissionsFormProvider>(TASK_FORM_PROVIDER);

    fixture = TestBed.createComponent(AviationEmissionsChargesCalculateComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should call the saveDre function with the correct data when the form is valid', async () => {
    const data = {
      ...store.dreDelegate.payload,
      determinationReason: {
        type: 'VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER',
        furtherDetails: 'further details',
      },
      totalReportableEmissions: 2000,
      calculationApproach: {
        type: 'OTHER_DATASOURCE',
        otherDataSourceExplanation: 'other data source explanation',
      },
      supportingDocuments: ['c5322719-abc1-419a-839e-eb9fbf27f277'],
      fee: {
        chargeOperator: true,
        feeDetails: {
          totalBillableHours: '2000.2',
          hourlyRate: '1.5',
          dueDate: new Date('2025-01-01'),
          comments: 'Further details fee',
        },
      },
    };

    (formProvider as any).addFee();

    formProvider.setFormValue({
      determinationReason: {
        type: 'VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER',
        furtherDetails: 'further details',
      },
      totalReportableEmissions: 2000,
      calculationApproach: {
        type: 'OTHER_DATASOURCE',
        otherDataSourceExplanation: 'other data source explanation',
      },
      supportingDocuments: ['c5322719-abc1-419a-839e-eb9fbf27f277'],
      fee: {
        chargeOperator: true,
        feeDetails: {
          totalBillableHours: '2000.2',
          hourlyRate: '1.5',
          dueDate: new Date('2025-01-01'),
          comments: 'Further details fee',
        },
      },
    } as any);

    const saveDreSpy = jest.spyOn(store.dreDelegate, 'saveDre').mockReturnValue(of({}));

    component.onSubmit();

    expect(saveDreSpy).toHaveBeenCalledWith({ dre: data }, 'in progress');

    saveDreSpy.mockRestore();
  });
});
