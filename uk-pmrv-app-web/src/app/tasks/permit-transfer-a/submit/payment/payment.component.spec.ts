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

import { TransferAPaymentComponent } from './payment.component';

describe('PaymentComponent', () => {
  let component: TransferAPaymentComponent;
  let fixture: ComponentFixture<TransferAPaymentComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<TransferAPaymentComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get reportOptions() {
      return this.queryAll<HTMLInputElement>('input[name$="payer"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransferAPaymentComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      imports: [PermitTransferAModule, RouterTestingModule],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockState });
    fixture = TestBed.createComponent(TransferAPaymentComponent);
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
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'aem-report'], { relativeTo: route });
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
  });

  it('should submit form and navigate to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.reportOptions[0].click();

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'aem-report'], { relativeTo: route });
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockPostBuild({ payer: 'TRANSFERER' }));
  });
});
