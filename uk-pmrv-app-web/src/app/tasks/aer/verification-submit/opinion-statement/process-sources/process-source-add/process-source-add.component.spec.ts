import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { noProcessSourceText } from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { OpinionStatementModule } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.module';
import { ProcessSourceAddComponent } from '@tasks/aer/verification-submit/opinion-statement/process-sources/process-source-add/process-source-add.component';
import { mockPostBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationVerificationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

describe('ProcessSourceAddComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: ProcessSourceAddComponent;
  let fixture: ComponentFixture<ProcessSourceAddComponent>;
  const tasksService = mockClass(TasksService);
  const expectedProcessSource = 'Test process source';

  class Page extends BasePage<ProcessSourceAddComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    set processSourceValue(value: string) {
      this.setInputValue('#processSource', value);
    }

    get inputElement(): HTMLInputElement {
      return this.query<HTMLInputElement>('input[name$="processSource"]');
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(ProcessSourceAddComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements and form with 0 errors', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Add a process source');
    expect(page.inputElement).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
    expect(page.errorSummary).toBeFalsy();
  });

  it('should display error on empty form submit', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryListContents).toEqual([noProcessSourceText]);
    expect(page.errorSummaryListContents.length).toEqual(1);
  });

  it('should submit a valid form and navigate to previous route', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.processSourceValue = expectedProcessSource;
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
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
    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute });
  });
});
