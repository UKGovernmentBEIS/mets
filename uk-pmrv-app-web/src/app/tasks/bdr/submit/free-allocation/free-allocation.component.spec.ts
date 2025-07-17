import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../testing/mock-state';
import { FreeAllocationComponent } from './free-allocation.component';

describe('FreeAllocationComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: FreeAllocationComponent;
  let fixture: ComponentFixture<FreeAllocationComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

  class Page extends BasePage<FreeAllocationComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get isApplicationForFreeAllocation() {
      return this.query<HTMLInputElement>('#isApplicationForFreeAllocation-option0');
    }

    get statusApplicationType() {
      return this.query<HTMLInputElement>('#statusApplicationType-option0');
    }

    get checkboxes() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
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
    fixture = TestBed.createComponent(FreeAllocationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new free allocation details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          bdr: {
            files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
          },
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Free allocation and emitter status');
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([
        'Select yes if you are applying for free allocation',
        'Select whether you are applying for HSE or USE status',
        'Confirm that the information provided is correct',
      ]);
      expect(page.errorSummaryListContents.length).toEqual(3);
    });

    it('should submit a valid form and navigate to next page', async () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.isApplicationForFreeAllocation.click();
      page.statusApplicationType.click();
      page.checkboxes[0].click();

      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            bdr: {
              files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
              isApplicationForFreeAllocation: true,
              statusApplicationType: 'NONE',
              infoIsCorrectChecked: true,
              hasMmp: null,
            },
          },
          {
            baseline: false,
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../provide-mmp'], { relativeTo: activatedRoute });
    });
  });
});
