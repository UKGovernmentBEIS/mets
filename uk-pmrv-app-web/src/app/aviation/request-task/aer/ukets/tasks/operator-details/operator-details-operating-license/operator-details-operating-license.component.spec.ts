import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass, MockType } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';
import produce from 'immer';

import { IssuingAuthoritiesService, OperatingLicense, TasksService } from 'pmrv-api';

import { OperatorDetailsFormProvider } from '../operator-details-form.provider';
import { OperatorDetailsOperatingLicenseComponent } from './operator-details-operating-license.component';

describe('OperatorDetailsOperatingLicenseComponent', () => {
  let component: OperatorDetailsOperatingLicenseComponent;
  let fixture: ComponentFixture<OperatorDetailsOperatingLicenseComponent>;
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
      imports: [OperatorDetailsOperatingLicenseComponent, RouterTestingModule],
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
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
            payload: AerUkEtsStoreDelegate.INITIAL_STATE as AerRequestTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(OperatorDetailsOperatingLicenseComponent);
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
    expect(screen.getAllByText(/Say if you have an operating licence or not/)).toHaveLength(2);

    await user.click(screen.getByRole('radio', { name: /Yes/ }));
    fixture.detectChanges();

    expect(screen.getAllByText(/Enter the operating licence number/)).toHaveLength(2);
    expect(screen.getAllByText(/Select the authority that issued your operating licence/)).toHaveLength(1);
  });

  it('should call the saveAer function with the correct data when the form is valid and navigate to organisation structure page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const operatingLicense = {
      licenseExist: true,
      licenseNumber: 'Licence number',
      issuingAuthority: issuingAuthority[0],
    } as OperatingLicense;

    const data = {
      operatorDetails: {
        operatingLicense,
      },
    };

    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.form.setValue(operatingLicense);
    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith(data, 'in progress');
    expect(navigateSpy).toHaveBeenCalledWith(['../', 'organisation-structure'], { relativeTo: activatedRouteStub });
  });
});
