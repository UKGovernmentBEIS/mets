import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';

import { ActivitiesDescription, PartnershipOrganisation, TasksService } from 'pmrv-api';

import { OperatorDetailsFormProvider } from '../operator-details-form.provider';
import { OperatorDetailsActivitiesDescriptionComponent } from './operator-details-activities-description.component';

describe('OperatorDetailsActivitiesDescriptionComponent', () => {
  let component: OperatorDetailsActivitiesDescriptionComponent;
  let fixture: ComponentFixture<OperatorDetailsActivitiesDescriptionComponent>;
  let store: RequestTaskStore;
  let user: UserEvent;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailsActivitiesDescriptionComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
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

    fixture = TestBed.createComponent(OperatorDetailsActivitiesDescriptionComponent);
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
    expect(screen.getAllByText(/Select if you are a commercial or non-commercial operator/)).toHaveLength(2);
    expect(screen.getAllByText(/Select if you operate scheduled or non-scheduled flights/)).toHaveLength(2);
    expect(screen.getAllByText(/Select if your scope of operation is UK domestic or UK to EEA countries/)).toHaveLength(
      2,
    );
    expect(screen.getAllByText(/Describe what kind of activities you carry out/)).toHaveLength(2);

    await user.click(screen.getByRole('radio', { name: /Commercial/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Select if you operate scheduled or non-scheduled flights/)).toHaveLength(2);
    expect(screen.getAllByText(/Select if your scope of operation is UK domestic or UK to EEA countries/)).toHaveLength(
      2,
    );
    expect(screen.getAllByText(/Describe what kind of activities you carry out/)).toHaveLength(2);

    await user.click(screen.getByRole('checkbox', { name: /Scheduled flights/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Select if your scope of operation is UK domestic or UK to EEA countries/)).toHaveLength(
      2,
    );
    expect(screen.getAllByText(/Describe what kind of activities you carry out/)).toHaveLength(2);

    await user.click(screen.getByRole('checkbox', { name: /UK domestic/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Describe what kind of activities you carry out/)).toHaveLength(2);
  });

  it('should call the saveEmp function with the correct data when the form is valid and navigate to summary page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const activitiesDescription = {
      operatorType: 'COMMERCIAL',
      flightTypes: ['SCHEDULED'],
      operationScopes: ['UK_DOMESTIC'],
      activityDescription: 'Activity description',
    } as ActivitiesDescription;

    const state = store.getState();

    const data = {
      operatorDetails: {
        ...(state.requestTaskItem.requestTask.payload as EmpRequestTaskPayloadUkEts).emissionsMonitoringPlan
          .operatorDetails,
        activitiesDescription,
      },
    };

    const saveEmpSpy = jest.spyOn(store.empUkEtsDelegate, 'saveEmp').mockReturnValue(of({}));

    component.form.setValue(activitiesDescription);
    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledTimes(1);
    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');
    expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], { relativeTo: activatedRouteStub });
  });
});
