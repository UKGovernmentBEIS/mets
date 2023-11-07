import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { OpinionStatementModule } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.module';
import { ProcessSourceDeleteComponent } from '@tasks/aer/verification-submit/opinion-statement/process-sources/process-source-delete/process-source-delete.component';
import { mockPostBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationVerificationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

describe('ProcessSourceDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: ProcessSourceDeleteComponent;
  let fixture: ComponentFixture<ProcessSourceDeleteComponent>;

  const processSources = (
    mockState.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
  ).verificationReport.opinionStatement.processSources;
  const expectedProcessSource = processSources[0];
  const route = new ActivatedRouteStub({ processSource: expectedProcessSource });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ProcessSourceDeleteComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get paragraph(): HTMLParagraphElement {
      return this.query<HTMLParagraphElement>('p');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }

    get cancelLink(): HTMLAnchorElement {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(ProcessSourceDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toContain('Are you sure you want to delete  ‘Calcination of lime’?');
    expect(page.paragraph).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
    expect(page.cancelLink).toBeTruthy();
  });

  it('should delete the process source and return to previous route', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          opinionStatement: (
            mockState.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
          ).verificationReport.opinionStatement,
        },
        { opinionStatement: [false] },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
