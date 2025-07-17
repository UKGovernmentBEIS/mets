import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { ConnectionDetailsComponent } from './connection-details.component';

describe('ConnectionDetailsComponent', () => {
  let component: ConnectionDetailsComponent;
  let fixture: ComponentFixture<ConnectionDetailsComponent>;
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1 }, null, {
    permitTask: 'monitoringMethodologyPlans',
  });

  class Page extends BasePage<ConnectionDetailsComponent> {
    get installationOrEntityName() {
      return this.getInputValue('#installationOrEntityName');
    }

    set installationOrEntityName(value: string) {
      this.setInputValue('#installationOrEntityName', value);
    }

    get entityType() {
      return this.query<HTMLInputElement>('#entityType-option0');
    }

    get connectionType() {
      return this.query<HTMLInputElement>('#connectionType-option0');
    }

    get flowDirection() {
      return this.query<HTMLInputElement>('#flowDirection-option0');
    }

    get installationId() {
      return this.getInputValue('#installationId');
    }

    set installationId(value: string) {
      this.setInputValue('#installationId', value);
    }

    get contactPersonName() {
      return this.getInputValue('#contactPersonName');
    }

    set contactPersonName(value: string) {
      this.setInputValue('#contactPersonName', value);
    }

    get emailAddress() {
      return this.getInputValue('#emailAddress');
    }

    set emailAddress(value: string) {
      this.setInputValue('#emailAddress', value);
    }

    get phoneNumber() {
      return this.getInputValue('#phoneNumber');
    }

    set phoneNumber(value: string) {
      this.setInputValue('#phoneNumber', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get title() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConnectionDetailsComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
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
              },
            },
          },
        },
        {
          monitoringMethodologyPlans: [true],
          mmpInstallationDescription: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(ConnectionDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new connection', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display add title', () => {
      expect(page.title).toEqual('Add a connection to an installation or entity');
    });

    it('should submit a valid form, update the store and navigate back to task', () => {
      expect(page.errorSummary).toBeFalsy();

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Enter the installation or entity name',
        'Select the installation or entity type',
        'Select the connection type',
        'Select the flow direction',
        'Enter a name',
        'Enter an email address',
        'Enter a phone number',
      ]);

      page.installationOrEntityName = 'name1';
      page.entityType.click();
      page.connectionType.click();
      page.flowDirection.click();
      page.installationId = '123';
      page.contactPersonName = '1';
      page.emailAddress = '1@1';
      page.phoneNumber = '1234567890';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
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

      expect(navigateSpy).toHaveBeenCalledWith(['../'], { relativeTo: activatedRoute });
    });
  });
});
