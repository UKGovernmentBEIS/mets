import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass, MockType } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';

import { IssuingAuthoritiesService, PartnershipOrganisation, TasksService } from 'pmrv-api';

import { OperatorDetailsFormProvider } from '../operator-details-form.provider';
import { OperatorDetailsAirOperatingCertificateComponent } from './operator-details-air-operating-certificate.component';

describe('OperatorDetailsAirOperatingCertificateComponent', () => {
  let component: OperatorDetailsAirOperatingCertificateComponent;
  let fixture: ComponentFixture<OperatorDetailsAirOperatingCertificateComponent>;
  let store: RequestTaskStore;
  let user: UserEvent;
  let router: Router;
  let issuingAuthoritiesService: MockType<IssuingAuthoritiesService>;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);
  const issuingAuthority = ['Australia - Civil Aviation Safety Authority', 'Bahrain - Civil Aviation Affairs'];

  beforeEach(async () => {
    issuingAuthoritiesService = {
      getEmpIssuingAuthorityNames: jest.fn().mockReturnValue(of(issuingAuthority)),
    };

    await TestBed.configureTestingModule({
      imports: [OperatorDetailsAirOperatingCertificateComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: IssuingAuthoritiesService, useValue: issuingAuthoritiesService },
      ],
    }).compileComponents();

    user = userEvent.setup();

    store = TestBed.inject(RequestTaskStore);

    const state = store.getState();

    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestTask: {
          type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
          payload: {
            emissionsMonitoringPlan: {
              ...EmpUkEtsStoreDelegate.INITIAL_STATE,
              operatorDetails: {
                organisationStructure: { partners: [] } as PartnershipOrganisation,
              },
            },
            reviewSectionsCompleted: {},
          } as any,
        },
      },
    });

    fixture = TestBed.createComponent(OperatorDetailsAirOperatingCertificateComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form errors', async () => {
    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(
      screen.getAllByText(/Select if you have an Air Operating Certificate or equivalent certification/),
    ).toHaveLength(2);

    await user.click(screen.getByRole('radio', { name: /Yes/ }));
    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Enter a certificate number/)).toHaveLength(2);
    expect(screen.getAllByText(/Select an issuing authority/)).toHaveLength(1);
  }, 50000);

  it('should call the saveEmp function with the correct data when the form is valid and navigate to operating license page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const airOperatingCertificate = {
      certificateExist: true,
      certificateNumber: 'Certificate number',
      issuingAuthority: issuingAuthority[0],
      certificateFiles: [
        {
          file: { name: 'New file' } as File,
          uuid: 'e19a616c-6040-4125-b96b-d8a5c393dd8f',
        },
      ],
    };

    const state = store.getState();

    const data = {
      operatorDetails: {
        ...(state.requestTaskItem.requestTask.payload as EmpRequestTaskPayloadUkEts).emissionsMonitoringPlan
          .operatorDetails,
        airOperatingCertificate: {
          ...airOperatingCertificate,
          certificateFiles: airOperatingCertificate.certificateFiles.map((file) => file.uuid),
        },
      },
    };

    const saveEmpSpy = jest.spyOn(store.empUkEtsDelegate, 'saveEmp').mockReturnValue(of({}));

    component.form.setValue(airOperatingCertificate);
    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledTimes(1);
    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');
    expect(navigateSpy).toHaveBeenCalledWith(['../', 'operating-license'], { relativeTo: activatedRouteStub });
  });
});
