import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitTransferAModule } from '@tasks/permit-transfer-a/permit-transfer-a.module';
import { mockPostBuild, mockState } from '@tasks/permit-transfer-a/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { TransferACodeComponent } from './code.component';

describe('TransferCodeComponent', () => {
  let component: TransferACodeComponent;
  let fixture: ComponentFixture<TransferACodeComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<TransferACodeComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    set transferCode(value: string) {
      this.setInputValue('#transferCode', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLLIElement>('ul.govuk-error-summary__list > li');
    }
  }

  const runOnPushChangeDetection = async (fixture: ComponentFixture<any>): Promise<void> => {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransferACodeComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      imports: [PermitTransferAModule, RouterTestingModule],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockState });
    fixture = TestBed.createComponent(TransferACodeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not submit form and navigate to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'summary'], { relativeTo: route });
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
  });

  it('should validate form and display an error message', async () => {
    page.transferCode = '12345678';
    page.submitButton.click();
    await runOnPushChangeDetection(fixture);

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Enter a valid transfer code']);
    expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
  });

  it('should submit form and navigate to next step', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.transferCode = '123456789';
    page.submitButton.click();
    await runOnPushChangeDetection(fixture);

    expect(page.errorSummary).toBeFalsy();
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'summary'], { relativeTo: route });
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockPostBuild({ transferCode: '123456789' }));
  });
});
