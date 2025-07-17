import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { NonConformitiesModule } from '@tasks/aer/verification-submit/non-conformities/non-conformities.module';
import { DeleteComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/delete/delete.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('DeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<DeleteComponent> {
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
      imports: [NonConformitiesModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild({
        uncorrectedNonConformities: {
          areThereUncorrectedNonConformities: true,
          uncorrectedNonConformities: [
            {
              reference: 'B1',
              explanation: 'Explanation 1',
              materialEffect: true,
            },
            {
              reference: 'B2',
              explanation: 'Explanation 2',
              materialEffect: false,
            },
          ],
        },
      }),
    );
    fixture = TestBed.createComponent(DeleteComponent);
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
    expect(page.heading1.textContent.trim()).toContain("Are you sure you want to delete 'B1 Explanation 1'?");
    expect(page.paragraph).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
    expect(page.cancelLink).toBeTruthy();
  });

  it('should delete the non-conformity and return to list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          uncorrectedNonConformities: {
            areThereUncorrectedNonConformities: true,
            uncorrectedNonConformities: [
              {
                explanation: 'Explanation 2',
                materialEffect: false,
                reference: 'B1',
              },
            ],
          },
        },
        { uncorrectedNonConformities: [false] },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../../list'], { relativeTo: activatedRoute });
  });
});
