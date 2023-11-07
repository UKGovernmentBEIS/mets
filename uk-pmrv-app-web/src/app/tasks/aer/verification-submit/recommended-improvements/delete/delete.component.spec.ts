import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RecommendedImprovementsModule } from '@tasks/aer/verification-submit/recommended-improvements/recommended-improvements.module';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { DeleteComponent } from './delete.component';

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
      imports: [RecommendedImprovementsModule, RouterTestingModule],
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
        recommendedImprovements: {
          areThereRecommendedImprovements: true,
          recommendedImprovements: [
            {
              reference: 'D1',
              explanation: 'Explanation 1',
            },
            {
              reference: 'D2',
              explanation: 'Explanation 2',
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
    expect(page.heading1.textContent.trim()).toContain("Are you sure you want to delete  'D1 Explanation 1'?");
    expect(page.paragraph).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
    expect(page.cancelLink).toBeTruthy();
  });

  it('should delete the recommended-improvement and return to list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          recommendedImprovements: {
            areThereRecommendedImprovements: true,
            recommendedImprovements: [
              {
                reference: 'D1',
                explanation: 'Explanation 2',
              },
            ],
          },
        },
        { recommendedImprovements: [false] },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../../list'], { relativeTo: activatedRoute });
  });
});
