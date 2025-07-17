import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import {
  inspectionForSubmitMockPostBuild,
  inspectionSubmitMockState,
  mockInspectionSubmitRequestTaskPayload,
} from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { FollowUpActionDeleteComponent } from './follow-up-action-delete.component';

describe('FollowUpActionDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  let component: FollowUpActionDeleteComponent;
  let fixture: ComponentFixture<FollowUpActionDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'audit', id: '0' });

  class Page extends BasePage<FollowUpActionDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FollowUpActionDeleteComponent, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionSubmitMockState);

    fixture = TestBed.createComponent(FollowUpActionDeleteComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the item name', () => {
    expect(page.header.textContent.trim()).toEqual(`Are you sure you want to remove follow-up action?`);
  });

  it('should delete and navigate to list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      inspectionForSubmitMockPostBuild(
        {
          installationInspection: {
            ...mockInspectionSubmitRequestTaskPayload.installationInspection,
            followUpActions: [
              {
                explanation: 'Vitae facere est as',
                followUpActionAttachments: [],
                followUpActionType: 'NON_CONFORMITY',
              },
            ],
            responseDeadline: '2026-02-20T00:00:00.000Z',
          },
        },
        {
          followUpAction: false,
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../', 'follow-up-actions'], { relativeTo: activatedRoute });
  });
});
