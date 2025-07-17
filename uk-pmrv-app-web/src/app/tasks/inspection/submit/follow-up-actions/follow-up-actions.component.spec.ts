import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { InspectionModule } from '@tasks/inspection/inspection.module';
import {
  inspectionForSubmitMockPostBuild,
  inspectionMockStateBuild,
  mockInspectionSubmitRequestTaskPayload,
} from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { FollowUpActionsComponent } from './follow-up-actions.component';

describe('FollowUpActionsComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: FollowUpActionsComponent;
  let fixture: ComponentFixture<FollowUpActionsComponent>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'audit', id: '0' }, null, {
    isCheckYourAnswersPage: true,
  });

  class Page extends BasePage<FollowUpActionsComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get followupitem() {
      return this.query('app-follow-up-item');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('#confirmBtn');
    }

    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InspectionModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      inspectionMockStateBuild({
        ...mockInspectionSubmitRequestTaskPayload,
        installationInspectionSectionsCompleted: {
          ...mockInspectionSubmitRequestTaskPayload.installationInspectionSectionsCompleted,
          followUpAction: false,
        },
      }),
    );

    fixture = TestBed.createComponent(FollowUpActionsComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements for follow up required true', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Check your answers');
    expect(page.followupitem).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
  });

  it('should display all HTMLElements for follow up required false', () => {
    store.setState(
      inspectionMockStateBuild({
        ...mockInspectionSubmitRequestTaskPayload,
        installationInspection: {
          ...mockInspectionSubmitRequestTaskPayload.installationInspection,
          followUpActionsRequired: false,
          followUpActionsOmissionFiles: [],
          followUpActionsOmissionJustification: 'text',
          followUpActions: [],
        },
        installationInspectionSectionsCompleted: {
          ...mockInspectionSubmitRequestTaskPayload.installationInspectionSectionsCompleted,
          followUpAction: false,
        },
      }),
    );
    fixture.detectChanges();
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Check your answers');
    expect(page.followupitem).toBeFalsy();
    expect(page.summaryValues).toEqual([
      ['Do you want to add follow-up actions for the operator?', 'No'],
      ['Justification', 'text'],
    ]);
    expect(page.submitButton).toBeTruthy();
  });

  it('should submit and navigate to next page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      inspectionForSubmitMockPostBuild(
        {
          installationInspection: {
            ...mockInspectionSubmitRequestTaskPayload.installationInspection,
          },
        },
        {
          ...mockInspectionSubmitRequestTaskPayload.installationInspectionSectionsCompleted,
          followUpAction: true,
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../'], { relativeTo: activatedRoute });
  });
});
