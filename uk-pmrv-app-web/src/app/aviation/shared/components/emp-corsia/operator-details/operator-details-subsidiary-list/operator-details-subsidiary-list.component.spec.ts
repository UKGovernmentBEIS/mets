import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { OperatorDetailsCorsiaFormProvider } from '@aviation/request-task/emp/corsia/tasks/operator-details';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { EmpCorsiaOperatorDetails, TasksService } from 'pmrv-api';

import { OperatorDetailsSubsidiaryListComponent } from './operator-details-subsidiary-list.component';

describe('OperatorDetailsSubsidiaryListComponent', () => {
  let component: OperatorDetailsSubsidiaryListComponent;
  let fixture: ComponentFixture<OperatorDetailsSubsidiaryListComponent>;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailsSubsidiaryListComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsCorsiaFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(OperatorDetailsSubsidiaryListComponent);
    component = fixture.componentInstance;
    component.subsidiaryCompaniesData$ = of([
      {
        operatorName: 'Operator name',
        flightIdentification: {
          flightIdentificationType: 'AIRCRAFT_REGISTRATION_MARKINGS',
          aircraftRegistrationMarkings: 'Registration markings',
        },
        airOperatingCertificate: {
          certificateExist: true,
          certificateNumber: 'Certificate number',
          issuingAuthority: 'Cape Verde - Agência de Aviação Civil (AAC)',
          certificateFiles: [
            {
              file: {
                name: 'install.txt',
              },
              uuid: '711f822b-d2ac-4902-b352-2b918c5e4a24',
            },
          ],
          restrictionsExist: false,
          restrictionsDetails: null,
        },
        companyRegistrationNumber: 'Company registration number',
        registeredLocation: {
          type: 'ONSHORE_STATE',
          line1: 'Line 1',
          line2: null,
          city: 'City',
          state: null,
          postcode: null,
          country: 'AF',
        },
        flightTypes: ['SCHEDULED'],
        activityDescription: 'Activity description',
      },
    ] as unknown as EmpCorsiaOperatorDetails['subsidiaryCompanies']);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
