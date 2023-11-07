import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ApprovedAllocationsComponent } from '@tasks/doal/authority-response/response/approved-allocations/approved-allocations.component';
import { DoalAuthorityTaskSectionKey } from '@tasks/doal/core/doal-task.type';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { mockDoalAuthorityResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { DoalAuthorityResponseRequestTaskPayload, TasksService } from 'pmrv-api';

describe('ApprovedAllocationsComponent', () => {
  let component: ApprovedAllocationsComponent;
  let fixture: ComponentFixture<ApprovedAllocationsComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ApprovedAllocationsComponent> {
    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  function createComponent() {
    fixture = TestBed.createComponent(ApprovedAllocationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApprovedAllocationsComponent, DoalTaskComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
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

  it('should display data', () => {
    expect(page.tableValues).toEqual([[], ['2023', '100'], ['2024', '200']]);
  });

  it('should submit and navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

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
            mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload
          ).doalAuthority,
          authorityResponse: {
            ...(
              mockDoalAuthorityResponseRequestTaskTaskItem.requestTask
                .payload as DoalAuthorityResponseRequestTaskPayload
            ).doalAuthority.authorityResponse,
            documents: [],
            totalAllocationsPerYear: {
              '2023': 100,
              '2024': 200,
            },
          },
        },
        doalSectionsCompleted: {
          dateSubmittedToAuthority: true,
          authorityResponse: false,
        } as { [key in DoalAuthorityTaskSectionKey]: boolean },
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], {
      relativeTo: route,
      state: { enableViewSummary: true },
    });
  });
});
