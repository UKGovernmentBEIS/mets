import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ConnectionDeleteComponent } from './connection-delete.component';

describe('ConnectionDeleteComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  let component: ConnectionDeleteComponent;
  let fixture: ComponentFixture<ConnectionDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, connectionIndex: '0' }, null, {
    permitTask: 'monitoringMethodologyPlans',
  });

  class Page extends BasePage<ConnectionDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConnectionDeleteComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitIssuanceStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
            digitizedPlan: {
              installationDescription: {
                description: 'description',
                flowDiagrams: ['e227ea8a-778b-4208-9545-e108ea66c113'],
                connections: [
                  {
                    entityType: 'ETS_INSTALLATION',
                    phoneNumber: '1234567890',
                    connectionNo: '0',
                    emailAddress: '1@1',
                    flowDirection: 'IMPORT',
                    connectionType: 'MEASURABLE_HEAT',
                    installationId: '123',
                    contactPersonName: '1',
                    installationOrEntityName: 'name1',
                  },
                ],
              },
            },
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          mmpInstallationDescription: [false],
        },
      ),
    );

    fixture = TestBed.createComponent(ConnectionDeleteComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the item name', () => {
    expect(page.header.textContent.trim()).toEqual(`Are you sure you want to delete  '1' ?`);
  });

  it('should delete and navigate to list', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
  });
});
