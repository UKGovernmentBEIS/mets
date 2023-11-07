import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { parseCsv } from '@aviation/request-task/util';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';
import produce from 'immer';

import { OperatingLicense, PartnershipOrganisation, TasksService } from 'pmrv-api';

import { OperatorDetailsFormProvider } from '../operator-details-form.provider';
import { OperatorDetailsSummaryComponent } from './operator-details-summary.component';

describe('OperatorDetailsSummaryComponent', () => {
  let component: OperatorDetailsSummaryComponent;
  let fixture: ComponentFixture<OperatorDetailsSummaryComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const operatorName = {
    operatorName: 'Operator name',
    crcoCode: 'CRCO code',
  };

  const flightIdentification = {
    flightIdentificationType: 'AIRCRAFT_REGISTRATION_MARKINGS',
    aircraftRegistrationMarkings: 'Code line 1\nCode line 2',
  };

  const airOperatingCertificate = {
    certificateExist: false,
  };

  const operatingLicense = {
    licenseExist: false,
  } as OperatingLicense;

  const organisationStructure = {
    legalStatusType: 'PARTNERSHIP',
    organisationLocation: {
      type: 'ONSHORE_STATE',
      line1: 'Address Line 1',
      line2: 'Address Line 2',
      city: 'Town or city',
      state: 'State',
      postcode: 'Postcode',
      country: 'CY',
    },
    partnershipName: 'Partnership name',
    partners: ['Partner 1'],
  } as PartnershipOrganisation;

  const transformForm = () => {
    const form = component.form.value;

    return {
      ...form,
      operatorName: form.operatorName.operatorName,
      crcoCode: form.operatorName.crcoCode,
      flightIdentification: {
        ...form.flightIdentification,
        aircraftRegistrationMarkings: parseCsv(flightIdentification.aircraftRegistrationMarkings),
      },
    };
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailsSummaryComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            payload: AerStoreDelegate.INITIAL_STATE as AerRequestTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(OperatorDetailsSummaryComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call saveAer on submit and redirect to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.form.patchValue({
      operatorName,
      flightIdentification,
      airOperatingCertificate,
      operatingLicense,
      organisationStructure,
    });

    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ operatorDetails: transformForm() }, 'complete');
    expect(navigateSpy).toHaveBeenCalledWith(['../../../'], { relativeTo: activatedRouteStub });
  });
});
