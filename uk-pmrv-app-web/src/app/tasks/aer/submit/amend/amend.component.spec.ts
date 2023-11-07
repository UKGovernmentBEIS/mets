import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, UrlSegment } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-apply-action';
import { mockPostBuild } from '../testing/mock-state';
import { AmendComponent } from './amend.component';

describe('AmendComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let route: ActivatedRoute;

  let component: AmendComponent;
  let fixture: ComponentFixture<AmendComponent>;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 1, section: 'FUELS_AND_EQUIPMENT' };

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AmendComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get madeChanges() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AmendComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    const route = new ActivatedRouteStub({ section: 'FUELS_AND_EQUIPMENT' });

    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('selecting monitoring approaches', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Check the box to confirm you have made changes and want to mark as complete',
      ]);

      page.madeChanges[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {},
          {
            AMEND_FUELS_AND_EQUIPMENT: [true],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
      });
    });
  });
});
