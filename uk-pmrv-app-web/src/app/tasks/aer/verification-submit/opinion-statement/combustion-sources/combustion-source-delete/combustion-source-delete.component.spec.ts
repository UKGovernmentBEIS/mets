import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CombustionSourceDeleteComponent } from '@tasks/aer/verification-submit/opinion-statement/combustion-sources/combustion-source-delete/combustion-source-delete.component';
import { OpinionStatementModule } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.module';
import { mockPostBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationVerificationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

describe('CombustionSourceDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: CombustionSourceDeleteComponent;
  let fixture: ComponentFixture<CombustionSourceDeleteComponent>;

  const combustionSources = (
    mockState.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
  ).verificationReport.opinionStatement.combustionSources;
  const expectedCombustionSource = combustionSources[0];
  const route = new ActivatedRouteStub({ combustionSource: expectedCombustionSource });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CombustionSourceDeleteComponent> {
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
    fixture = TestBed.createComponent(CombustionSourceDeleteComponent);
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
    expect(page.heading1.textContent.trim()).toContain('Are you sure you want to delete  ‘Refinery fuel’?');
    expect(page.paragraph).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
    expect(page.cancelLink).toBeTruthy();
  });

  it('should delete the combustion source and return to previous route', () => {
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
