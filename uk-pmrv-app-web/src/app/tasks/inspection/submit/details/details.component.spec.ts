import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@core/store';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import {
  inspectionForSubmitMockPostBuild,
  inspectionMockStateBuild,
  mockInspectionSubmitRequestTaskPayload,
  mockOfficers,
} from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksAssignmentService, TasksService } from 'pmrv-api';

import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let authStore: AuthStore;

  const tasksService = mockClass(TasksService);
  const tasksAssignmentService = mockClass(TasksAssignmentService);

  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'audit', id: '0' });

  class Page extends BasePage<DetailsComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    set officerName1(value: string) {
      this.setInputValue('#officerName1', value);
    }

    get officerName1() {
      return this.getInputValue('#officerName1');
    }

    get officerName1List() {
      return Array.from(this.query<HTMLDivElement>('#officerName1').querySelectorAll('option')).map((item) =>
        item.textContent.trim(),
      );
    }

    set officerName2(value: string) {
      this.setInputValue('#officerName2', value);
    }

    get officerName2() {
      return this.getInputValue('#officerName2');
    }

    get officerName2List() {
      return Array.from(this.query<HTMLDivElement>('#officerName2').querySelectorAll('option')).map((item) =>
        item.textContent.trim(),
      );
    }

    set additionalInformation(value: string) {
      this.setInputValue('#additionalInformation', value);
    }

    get additionalInformation() {
      return this.getInputValue('#additionalInformation');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;

    authStore = TestBed.inject(AuthStore);
    authStore.setUserProfile(mockOfficers[0]);

    page = new Page(fixture);

    fixture.detectChanges();
  };

  beforeEach(async () => {
    tasksAssignmentService.getCandidateAssigneesByTaskType.mockReturnValue(of(mockOfficers));

    await TestBed.configureTestingModule({
      imports: [DetailsComponent, RouterTestingModule],
      providers: [
        KeycloakService,
        TaskTypeToBreadcrumbPipe,
        {
          provide: ActivatedRoute,
          useValue: activatedRoute,
        },
        { provide: TasksService, useValue: tasksService },
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
      ],
    }).compileComponents();
  });

  describe('for a new details action', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        inspectionMockStateBuild({
          installationInspection: {},
        }),
      );
      createComponent();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });
    it('should submit a valid form and navigate to next page', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();
      expect(page.submitButton).toBeTruthy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      expect(page.officerName1List).toEqual(['newreg1 User', 'newreg2 User', 'newreg3 User']);
      expect(page.officerName2List).toEqual(['newreg1 User', 'newreg2 User', 'newreg3 User']);

      page.officerName1 = `1: ${mockOfficers[1].firstName + ' ' + mockOfficers[1].lastName}`;
      page.officerName2 = `2: ${mockOfficers[2].firstName + ' ' + mockOfficers[2].lastName}`;
      page.additionalInformation =
        mockInspectionSubmitRequestTaskPayload.installationInspection.details.additionalInformation;

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        inspectionForSubmitMockPostBuild(
          {
            installationInspection: {
              details: {
                ...mockInspectionSubmitRequestTaskPayload.installationInspection.details,
                officerNames: [
                  mockOfficers[1].firstName + ' ' + mockOfficers[1].lastName,
                  mockOfficers[2].firstName + ' ' + mockOfficers[2].lastName,
                ],
                additionalInformation:
                  mockInspectionSubmitRequestTaskPayload.installationInspection.details.additionalInformation,
              },
            },
          },
          {
            details: false,
            followUpAction: true,
          },
        ),
      );
    });
  });
});
