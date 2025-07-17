import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { OperatorDetailsSubsidiaryListComponent } from '@aviation/shared/components/emp-corsia/operator-details/operator-details-subsidiary-list/operator-details-subsidiary-list.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { OperatorDetailsCorsiaFormProvider } from '../operator-details-form.provider';
import { OperatorDetailsSubsidiaryCompaniesComponent } from './operator-details-subsidiary-companies.component';

describe('OperatorDetailsSubsidiaryCompaniesComponent', () => {
  let component: OperatorDetailsSubsidiaryCompaniesComponent;
  let fixture: ComponentFixture<OperatorDetailsSubsidiaryCompaniesComponent>;
  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);
  const destroySubject = mockClass(DestroySubject);
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailsSubsidiaryListComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsCorsiaFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: DestroySubject, useValue: destroySubject },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    const state = store.getState();

    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestTask: {
          type: 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT',
          payload: {
            emissionsMonitoringPlan: {
              ...EmpCorsiaStoreDelegate.INITIAL_STATE,
              // operatorDetails: {
              //   organisationStructure: { partners: [] } as PartnershipOrganisation,
              // },
            },
            reviewSectionsCompleted: {},
          } as any,
        },
      },
    });

    fixture = TestBed.createComponent(OperatorDetailsSubsidiaryCompaniesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
