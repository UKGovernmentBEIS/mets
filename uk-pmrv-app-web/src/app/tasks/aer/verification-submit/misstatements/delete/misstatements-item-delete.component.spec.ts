import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { MisstatementsItemDeleteComponent } from '@tasks/aer/verification-submit/misstatements/delete/misstatements-item-delete.component';
import { MisstatementsModule } from '@tasks/aer/verification-submit/misstatements/misstatements.module';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

describe('MisstatementsItemDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: MisstatementsItemDeleteComponent;
  let fixture: ComponentFixture<MisstatementsItemDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<MisstatementsItemDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisstatementsModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(async () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild({
        uncorrectedMisstatements: {
          areThereUncorrectedMisstatements: true,
          uncorrectedMisstatements: [
            {
              reference: 'A1',
              explanation: 'Explanation 1',
              materialEffect: false,
            },
          ],
        },
      }),
    );

    fixture = TestBed.createComponent(MisstatementsItemDeleteComponent);
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

  it('should display the item name', () => {
    expect(page.header.textContent.trim()).toEqual(`Are you sure you want to delete 'A1 Explanation 1'?`);
  });

  it('should delete and navigate to list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          uncorrectedMisstatements: {
            areThereUncorrectedMisstatements: true,
            uncorrectedMisstatements: [],
          },
        },
        { uncorrectedMisstatements: [false] },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../list'], { relativeTo: activatedRoute });
  });
});
