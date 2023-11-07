import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { noRegulatedActivitySelected } from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { OpinionStatementModule } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.module';
import { RegulatedActivityAddComponent } from '@tasks/aer/verification-submit/opinion-statement/regulated-activities/regulated-activity-add/regulated-activity-add.component';
import { mockPostBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationVerificationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

describe('RegulatedActivityAddComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: RegulatedActivityAddComponent;
  let fixture: ComponentFixture<RegulatedActivityAddComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<RegulatedActivityAddComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get categoryInputs(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="activityCategory"]');
    }

    get activityInputs(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="activity"]');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }, DestroySubject],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(RegulatedActivityAddComponent);
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
    expect(page.heading1.textContent.trim()).toEqual('Select a regulated activity used at the installation');
    expect(page.categoryInputs).toHaveLength(8);
    expect(page.activityInputs).toHaveLength(26);
    expect(page.submitButton).toBeTruthy();
    expect(page.errorSummary).toBeFalsy();
  });

  it('should display error when nothing selected', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryListContents).toEqual([noRegulatedActivitySelected]);
    expect(page.errorSummaryListContents.length).toEqual(1);
  });

  it('should display error when already existing category gets selected', () => {
    page.categoryInputs[0].click();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();

    expect(page.errorSummaryListContents.length).toEqual(1);
  });

  it('should submit a valid form and navigate to previous route', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.activityInputs[24].click();
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
