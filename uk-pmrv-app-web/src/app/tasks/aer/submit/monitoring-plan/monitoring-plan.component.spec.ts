import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockStateBuild } from '../testing/mock-state';
import { MonitoringPlanComponent } from './monitoring-plan.component';

describe('MonitoringPlanComponent', () => {
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let component: MonitoringPlanComponent;
  let fixture: ComponentFixture<MonitoringPlanComponent>;
  let store: CommonTasksStore;
  let aerService: AerService;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<MonitoringPlanComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="existChangesNotCoveredInApprovedVariations"]');
    }

    get details() {
      return this.query<HTMLTextAreaElement>('textarea');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errors() {
      return Array.from(this.errorSummary.querySelectorAll('a'));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateBuild());
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitoringPlanComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    aerService = TestBed.inject(AerService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show/hide the details input', () => {
    page.existRadios[0].click();
    fixture.detectChanges();
    expect(page.details.disabled).toBeFalsy();

    page.existRadios[1].click();
    fixture.detectChanges();

    expect(page.details.disabled).toBeTruthy();
  });

  it('should validate and submit the data', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');

    component.form.get('existChangesNotCoveredInApprovedVariations').setValue(true);
    component.form.get('details').setValue('the details');
    component.form.markAsDirty();
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
    expect(postTaskSaveSpy).toHaveBeenCalledWith(
      {
        aerMonitoringPlanDeviation: {
          existChangesNotCoveredInApprovedVariations: true,
          details: 'the details',
        },
      },
      undefined,
      false,
      'aerMonitoringPlanDeviation',
    );

    expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
      relativeTo: activatedRoute,
    });
  });

  it('should require all fields to be populated', () => {
    component.form.get('existChangesNotCoveredInApprovedVariations').setValue(null);
    component.form.get('details').setValue(null);
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Select yes or no']);

    page.existRadios[0].click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Enter details']);
  });
});
