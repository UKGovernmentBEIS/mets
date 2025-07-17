import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import {
  noDate,
  noDuplicatedDate,
  noTeamMembersDescription,
  todayOrPastDate,
} from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { OpinionStatementModule } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.module';
import { InPersonVisitComponent } from '@tasks/aer/verification-submit/opinion-statement/site-visits/in-person-visit/in-person-visit.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { addDays, subDays } from 'date-fns';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('InPersonVisitComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: InPersonVisitComponent;
  let fixture: ComponentFixture<InPersonVisitComponent>;

  const tasksService = mockClass(TasksService);
  const expectedNextRoute = '../../summary';

  class Page extends BasePage<InPersonVisitComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get visitDates() {
      return Array.from(this.queryAll<HTMLFieldSetElement>("fieldset[id^='visitDates']")).map((fieldset, index) => {
        const day = this.getInputValue(`#visitDates.${index}-day`);
        const month = this.getInputValue(`#visitDates.${index}-month`);
        const year = this.getInputValue(`#visitDates.${index}-year`);
        return new Date(`${day}-${month}-${year}`);
      });
    }

    set visitDates(dates: Date[]) {
      dates.map((date, index) => {
        const day = date.getDate();
        const month = date.getMonth() + 1;
        const year = date.getFullYear();
        this.setInputValue(`#visitDates.${index}-day`, day);
        this.setInputValue(`#visitDates.${index}-month`, month);
        this.setInputValue(`#visitDates.${index}-year`, year);
      });
    }

    get teamMembers() {
      return this.getInputValue('#teamMembers');
    }

    set teamMembers(value: string) {
      this.setInputValue('#teamMembers', value);
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get removeButtons(): HTMLButtonElement[] {
      return this.queryAll<HTMLButtonElement>('button[type="button"]').filter(
        (el) => el.textContent.trim() === 'Remove',
      );
    }

    get addAnotherButton(): HTMLButtonElement {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (el) => el.textContent.trim() === 'Add another date',
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(InPersonVisitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }, DestroySubject],
    }).compileComponents();
  });

  describe('for new in-person-visit details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          opinionStatement: {},
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display headings, inputs and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1.textContent.trim()).toEqual('In person site visit details');
      expect(page.removeButtons).toHaveLength(0);
      expect(page.addAnotherButton).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noTeamMembersDescription, noDate]);
      expect(page.errorSummaryListContents.length).toEqual(2);
    });

    it('should display error on future and duplicate dates', () => {
      const day1 = addDays(new Date(), 1);
      const day2 = new Date();

      page.teamMembers = 'List of teamMembers';
      page.visitDates = [day1];
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([todayOrPastDate]);
      expect(page.errorSummaryListContents.length).toEqual(1);

      page.addAnotherButton.click();
      fixture.detectChanges();
      page.visitDates = [day2, day2];
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noDuplicatedDate]);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to `summary`', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const day1 = new Date(
        Date.UTC(
          subDays(new Date(), 1).getUTCFullYear(),
          subDays(new Date(), 1).getUTCMonth(),
          subDays(new Date(), 1).getUTCDate(),
        ),
      );
      const day2 = new Date(Date.UTC(new Date().getUTCFullYear(), new Date().getUTCMonth(), new Date().getUTCDate()));

      page.teamMembers = 'List of teamMembers';
      page.addAnotherButton.click();
      fixture.detectChanges();
      page.visitDates = [day1, day2];
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            opinionStatement: {
              siteVisit: {
                siteVisitType: 'IN_PERSON',
                teamMembers: 'List of teamMembers',
                visitDates: [day1, day2],
              },
            },
          },
          { opinionStatement: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing in-person-visit details', () => {
    const day1 = new Date('2020-12-01');
    const day2 = new Date('2021-02-28');

    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          opinionStatement: {
            siteVisit: {
              siteVisitType: 'IN_PERSON',
              teamMembers: 'List of teamMembers',
              visitDates: [day1, day2],
            },
          },
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display headings, inputs and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1.textContent.trim()).toEqual('In person site visit details');
      expect(page.removeButtons).toHaveLength(2);
      expect(page.addAnotherButton).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should submit a valid form and navigate to `summary`', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });

    it('should remove 1 date and submit form', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.removeButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            opinionStatement: {
              siteVisit: {
                siteVisitType: 'IN_PERSON',
                teamMembers: 'List of teamMembers',
                visitDates: [day1],
              },
            },
          },
          { opinionStatement: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to `summary`', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const newDay2 = new Date(
        Date.UTC(
          subDays(new Date(), 1).getUTCFullYear(),
          subDays(new Date(), 1).getUTCMonth(),
          subDays(new Date(), 1).getUTCDate(),
        ),
      );

      page.teamMembers = 'List of teamMembers edited';
      fixture.detectChanges();
      page.visitDates = [day1, newDay2];
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            opinionStatement: {
              siteVisit: {
                siteVisitType: 'IN_PERSON',
                teamMembers: 'List of teamMembers edited',
                visitDates: [day1, newDay2],
              },
            },
          },
          { opinionStatement: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
