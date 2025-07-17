import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../testing/mock-state';
import { MmpInstallationDescriptionAnswersComponent } from './mmp-installation-description-answers.component';

describe('MmpInstallationDescriptionAnswersComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: MmpInstallationDescriptionAnswersComponent;
  let fixture: ComponentFixture<MmpInstallationDescriptionAnswersComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<MmpInstallationDescriptionAnswersComponent> {
    get heading() {
      return this.query<HTMLElement>('.govuk-heading-l');
    }
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get confirmButton() {
      return this.queryAll<HTMLButtonElement>('.govuk-button')[1];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MmpInstallationDescriptionAnswersComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
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
    fixture = TestBed.createComponent(MmpInstallationDescriptionAnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit task status', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();
    fixture.detectChanges();

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
          mmpInstallationDescription: [true],
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route, state: { notification: true } });
  });
});
