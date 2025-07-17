import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { CountryService } from '@core/services/country.service';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, CountryServiceStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';

import { PartnershipOrganisation, TasksService } from 'pmrv-api';

import { OperatorDetailsCorsiaFormProvider } from '../operator-details-form.provider';
import { OperatorDetailsOrganisationStructureComponent } from './operator-details-organisation-structure.component';

describe('OperatorDetailsOrganisationStructureComponent', () => {
  let component: OperatorDetailsOrganisationStructureComponent;
  let fixture: ComponentFixture<OperatorDetailsOrganisationStructureComponent>;
  let store: RequestTaskStore;
  let user: UserEvent;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailsOrganisationStructureComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsCorsiaFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: CountryService, useClass: CountryServiceStub },
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

    fixture = TestBed.createComponent(OperatorDetailsOrganisationStructureComponent);
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
    expect(screen.getAllByText(/Select the option which shows the legal status of your organisation/)).toHaveLength(2);

    await user.click(screen.getByRole('radio', { name: /Limited company/ }));
    fixture.detectChanges();

    expect(screen.getAllByText(/Enter the company registration number/)).toHaveLength(2);
    expect(screen.getAllByText(/Say if you would like to enter a different contact address/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the first line of your address/)).toHaveLength(4);
    expect(screen.getAllByText(/Enter your town or city/)).toHaveLength(4);
    expect(screen.getAllByText(/Enter your country/)).toHaveLength(1);

    await user.click(screen.getByRole('radio', { name: /Individual/ }));
    fixture.detectChanges();

    expect(screen.getAllByText(/Enter full name/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the first line of your address/)).toHaveLength(4);
    expect(screen.getAllByText(/Enter your town or city/)).toHaveLength(4);
    expect(screen.getAllByText(/Enter your country/)).toHaveLength(1);

    await user.click(screen.getByRole('radio', { name: /Partnership/ }));
    fixture.detectChanges();

    expect(screen.getAllByText(/Enter the first line of your address/)).toHaveLength(4);
    expect(screen.getAllByText(/Enter your town or city/)).toHaveLength(4);
    expect(screen.getAllByText(/Enter your country/)).toHaveLength(1);
    expect(screen.getAllByText(/Enter the name of partner/)).toHaveLength(2);
  });

  it('should call the saveEmp function with the correct data when the form is valid and navigate to organisation structure page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const organisationStructure = {
      legalStatusType: 'PARTNERSHIP',
      organisationLocation: {
        type: 'ONSHORE_STATE',
        line1: 'Line 1',
        line2: 'Line 2',
        city: 'Town or city',
        state: 'State',
        postcode: 'Postcode',
        country: 'CY',
      },
      partnershipName: 'Partnership name',
      partners: ['Partner 1'],
    } as PartnershipOrganisation;

    const data = {
      operatorDetails: {
        organisationStructure,
      },
    };

    const saveEmpSpy = jest.spyOn(store.empCorsiaDelegate, 'saveEmp').mockReturnValue(of({}));

    component.form.setValue({
      ...organisationStructure,
      registrationNumber: null,
      evidenceFiles: [],
      differentContactLocationExist: null,
      differentContactLocation: {
        type: 'ONSHORE_STATE',
        line1: null,
        line2: null,
        city: null,
        state: null,
        postcode: null,
        country: null,
      },
      fullName: null,
    });
    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledTimes(1);
    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');
    expect(navigateSpy).toHaveBeenCalledWith(['../activities-description'], {
      relativeTo: activatedRouteStub,
      queryParams: { change: null },
    });
  });
});
