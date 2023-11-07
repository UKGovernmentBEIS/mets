import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { FallbackModule } from '@tasks/aer/submit/fallback/fallback.module';
import { mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockPostBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get summaries() {
      return this.queryAll<HTMLDListElement>('dl dd').map((dd) => dd.textContent.trim());
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FallbackModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the html contents', () => {
    expect(page.summaries).toEqual([
      'the reference Anthracite,the other reference Biodiesels',
      'Change',
      'Yes',
      'Change',
      'Test description',
      'Change',
      '',
      'Change',
      '1.1 tonnes CO2e',
      'Change',
      '2.2 terajoules',
      'Change',
      '3.3 tonnes CO2e',
      'Change',
      '4.4 terajoules',
      'Change',
      '8.8 tonnes CO2e',
      'Change',
    ]);
  });

  it('should submit the form and mark it as completed', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockPostBuild({}, { FALLBACK: [true] }));
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
