import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockAerApplyPayload } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ConfidentialityStatement, TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../testing/mock-state';
import { ConfidentialityStatementComponent } from './confidentiality-statement.component';

describe('ConfidentialityStatementComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: ConfidentialityStatementComponent;
  let fixture: ComponentFixture<ConfidentialityStatementComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ConfidentialityStatementComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get sections() {
      return this.queryAll<HTMLInputElement>('input[name$="section"]');
    }

    get sectionValues() {
      return this.sections.map((input) => input.value);
    }

    set sectionValues(values: string[]) {
      this.sections.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get explanations() {
      return this.queryAll<HTMLTextAreaElement>('textarea');
    }

    get explanationValues() {
      return this.explanations.map((input) => input.value);
    }

    set explanationValues(values: string[]) {
      this.explanations.forEach((input, index) => this.setInputValue(`#${input.id}`, values[index]));
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errors() {
      return Array.from(this.errorSummary.querySelectorAll('a'));
    }

    get addAnotherButton() {
      const secondaryButtons = this.queryAll<HTMLButtonElement>('button[type="button"]');

      return secondaryButtons[secondaryButtons.length - 1];
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = (state?: ConfidentialityStatement) => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild({
        confidentialityStatement: state ?? mockAerApplyPayload.aer.confidentialityStatement,
      }),
    );

    fixture = TestBed.createComponent(ConfidentialityStatementComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();

    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  beforeEach(() => createComponent());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display an non empty form', () => {
    expect(page.existRadios.some((radio) => radio.checked)).toBeTruthy();
    expect(page.sections).toHaveLength(2);
  });

  it('should display one other section if yes is selected', () => {
    createComponent({ exist: false, confidentialSections: undefined });
    expect(page.sections).toHaveLength(1);
  });

  it('should clear sections if no is submitted', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.existRadios[1].click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        { confidentialityStatement: { confidentialSections: undefined, exist: false } },
        { confidentialityStatement: [false] },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
  });

  it('should require all fields to be populated', () => {
    page.sectionValues = [page.sectionValues[0], ''];
    page.explanationValues = ['', page.explanationValues[0]];
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Enter an explanation', 'Enter a section']);
  });

  it('should display the existing data', () => {
    expect(page.existRadios[0].checked).toBeTruthy();
    expect(page.sectionValues).toEqual(['Section 1', 'Section 2']);
    expect(page.explanationValues).toEqual(['Explanation 1', 'Explanation 2']);
  });

  it('should add another section', () => {
    page.addAnotherButton.click();
    fixture.detectChanges();

    expect(page.sections).toHaveLength(3);
    expect(page.explanations).toHaveLength(3);

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
  });
});
