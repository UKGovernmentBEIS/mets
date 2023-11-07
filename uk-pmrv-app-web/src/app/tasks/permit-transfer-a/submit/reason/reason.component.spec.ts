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

import { TransferAReasonComponent } from './reason.component';

describe('TransferReasonComponent', () => {
  let component: TransferAReasonComponent;
  let fixture: ComponentFixture<TransferAReasonComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<TransferAReasonComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    set reason(value: string) {
      this.setInputValue('#reason', value);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransferAReasonComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      imports: [PermitTransferAModule, RouterTestingModule],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockState });
    fixture = TestBed.createComponent(TransferAReasonComponent);
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
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'date'], { relativeTo: route });
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
  });

  it('should submit form and navigate to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.reason = 'New reason';

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'date'], { relativeTo: route });
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockPostBuild({ reason: 'New reason' }));
  });
});
