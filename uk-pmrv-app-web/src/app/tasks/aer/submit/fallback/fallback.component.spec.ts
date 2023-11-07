import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { noSelection, noSourceStream } from '@tasks/aer/submit/fallback/errors/fallback-validation.errors';
import { FallbackModule } from '@tasks/aer/submit/fallback/fallback.module';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { FallbackEmissions, TasksService } from 'pmrv-api';

import { FallbackComponent } from './fallback.component';

describe('FallbackComponent', () => {
  let component: FallbackComponent;
  let fixture: ComponentFixture<FallbackComponent>;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  const expectedNextRoute = 'description';
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1', index: '0' });

  class Page extends BasePage<FallbackComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get sourceStreams() {
      return this.fixture.componentInstance.form.get('sourceStreams').value;
    }

    set sourceStreams(value: string[]) {
      this.fixture.componentInstance.form.get('sourceStreams').setValue(value);
    }

    get containsRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="contains"]');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(FallbackComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FallbackModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new fallback emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              FALLBACK: {
                type: 'FALLBACK',
              },
            },
          },
          {
            FALLBACK: [false],
          },
        ),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it(`should submit a valid form, update the store and navigate to ${expectedNextRoute}`, () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1.textContent).toEqual('Define the emission network used in your fallback approach');

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([noSourceStream, noSelection]);

      page.sourceStreams = [mockAerApplyPayload.aer.sourceStreams[0].id];
      page.containsRadios[0].click();
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              FALLBACK: {
                reportableEmissions: '0',
                sourceStreams: [mockAerApplyPayload.aer.sourceStreams[0].id],
                type: 'FALLBACK',
                biomass: {
                  contains: true,
                },
              } as FallbackEmissions,
            },
          },
          {
            FALLBACK: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for editing existing fallback emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.sourceStreams).toEqual([
        mockAerApplyPayload.aer.sourceStreams[0].id,
        mockAerApplyPayload.aer.sourceStreams[1].id,
      ]);
      expect(page.containsRadios[0]).toBeTruthy();
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.containsRadios[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
