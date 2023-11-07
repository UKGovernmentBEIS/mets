import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { OpinionStatementModule } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.module';
import { mockPostBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { CombustionSourcesComponent } from './combustion-sources.component';

describe('CombustionSourcesComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: CombustionSourcesComponent;
  let fixture: ComponentFixture<CombustionSourcesComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CombustionSourcesComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get paragraph(): HTMLHeadingElement {
      return this.query<HTMLParagraphElement>('p');
    }

    get summaryListElement() {
      return this.query('app-aer-combustion-sources-group');
    }

    get continueButton(): HTMLButtonElement {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (el) => el.textContent.trim() === 'Continue',
      );
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
    fixture = TestBed.createComponent(CombustionSourcesComponent);
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
    expect(page.heading1.textContent.trim()).toEqual('List the installations combustion sources (fuel types)');
    expect(page.paragraph).toBeTruthy();
    expect(page.summaryListElement).toBeTruthy();
  });

  it('should navigate to process-sources', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.continueButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(null, { opinionStatement: [false] }),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../process-sources'], { relativeTo: activatedRoute });
  });
});
