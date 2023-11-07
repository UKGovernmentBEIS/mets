import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { NonConformitiesModule } from '@tasks/aer/verification-submit/non-conformities/non-conformities.module';
import { ListComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/list/list.component';
import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('ListComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ListComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get summaryListElement() {
      return this.query('app-non-conformities-per-plan-group');
    }

    get continueButton(): HTMLButtonElement {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (el) => el.textContent.trim() === 'Continue',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonConformitiesModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild({
        opinionStatement: {},
      }),
    );
    fixture = TestBed.createComponent(ListComponent);
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

  it('should display headings, paragraph and summary list', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Non-conformities with the approved monitoring plan');
    expect(page.summaryListElement).toBeTruthy();
    expect(page.continueButton).toBeTruthy();
  });

  it('should navigate to `previous-year`', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.continueButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../../previous-year'], { relativeTo: activatedRoute });
  });
});
