import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { PreliminaryAllocationComponent } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocation/preliminary-allocation.component';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { mockDoalAuthorityResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { DoalAuthorityResponseRequestTaskPayload, RequestTaskPayload, TasksService } from 'pmrv-api';

describe('PreliminaryAllocationComponent', () => {
  let component: PreliminaryAllocationComponent;
  let fixture: ComponentFixture<PreliminaryAllocationComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<PreliminaryAllocationComponent> {
    get subInstallationName(): string {
      return this.getInputValue('#subInstallationName');
    }
    set subInstallationName(value: string) {
      this.setInputValue('#subInstallationName', value);
    }

    get year(): string {
      return this.getInputValue('#year');
    }
    set year(value: string) {
      this.setInputValue('#year', value);
    }
    get yearSelect(): HTMLSelectElement {
      return this.query('select[name="year"]');
    }
    get yearOptions(): string[] {
      return Array.from(this.yearSelect.options).map((option) => option.textContent.trim());
    }

    get allowances(): string {
      return this.getInputValue('#allowances');
    }
    set allowances(value: string) {
      this.setInputValue('#allowances', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(PreliminaryAllocationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreliminaryAllocationComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new preliminary allocation', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockDoalAuthorityResponseRequestTaskTaskItem,
          requestTask: {
            ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask,
            payload: {
              ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.payload,
              doalAuthority: {
                authorityResponse: {
                  preliminaryAllocations: [],
                },
              },
              doalSectionsCompleted: {},
            } as RequestTaskPayload,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.year = '2025';
      page.subInstallationName = 'EAF_CARBON_STEEL';
      page.allowances = '10';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_AUTHORITY_RESPONSE',
        requestTaskId: mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
          doalAuthority: {
            authorityResponse: {
              preliminaryAllocations: [
                {
                  subInstallationName: 'EAF_CARBON_STEEL',
                  year: '2025',
                  allowances: 10,
                },
              ],
            },
          },
          doalSectionsCompleted: {
            authorityResponse: false,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
    });
  });

  describe('for editing preliminary allocation', () => {
    beforeEach(() => {
      const route = new ActivatedRouteStub({
        taskId: `${mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.id}`,
        index: '0',
      });
      TestBed.overrideProvider(ActivatedRoute, { useValue: route });
    });

    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: mockDoalAuthorityResponseRequestTaskTaskItem,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit updated data and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.year).toEqual(2024);
      expect(page.subInstallationName).toEqual('ALUMINIUM');
      expect(page.allowances).toEqual('200');

      page.year = '2026';
      page.subInstallationName = 'EAF_CARBON_STEEL';
      page.allowances = '11';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'DOAL_SAVE_AUTHORITY_RESPONSE',
        requestTaskId: mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'DOAL_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
          doalAuthority: {
            ...(
              mockDoalAuthorityResponseRequestTaskTaskItem.requestTask
                .payload as DoalAuthorityResponseRequestTaskPayload
            ).doalAuthority,
            authorityResponse: {
              ...(
                mockDoalAuthorityResponseRequestTaskTaskItem.requestTask
                  .payload as DoalAuthorityResponseRequestTaskPayload
              ).doalAuthority.authorityResponse,
              preliminaryAllocations: [
                {
                  year: '2026',
                  subInstallationName: 'EAF_CARBON_STEEL',
                  allowances: 11,
                },
                {
                  year: 2023,
                  allowances: 100,
                  subInstallationName: 'ALUMINIUM',
                },
              ],
            },
          },
          doalSectionsCompleted: {
            authorityResponse: false,
            dateSubmittedToAuthority: true,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
    });
  });
});
