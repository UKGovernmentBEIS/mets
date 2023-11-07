import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AggregatedConsumptionFlightDataFormProvider } from '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data-form.provider';
import { AggregatedConsumptionFlightDataSummaryComponent } from '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data-summary/aggregated-consumption-flight-data-summary.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';

import { AviationAerCorsiaApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

describe('AggregatedConsumptionFlightDataSummaryComponent', () => {
  let fixture: ComponentFixture<AggregatedConsumptionFlightDataSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AggregatedConsumptionFlightDataFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
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

    fixture = TestBed.createComponent(AggregatedConsumptionFlightDataSummaryComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
