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

import { TransferADateComponent } from './date.component';

describe('DateComponent', () => {
  let component: TransferADateComponent;
  let fixture: ComponentFixture<TransferADateComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<TransferADateComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get transferDateDay() {
      return this.getInputValue('#transferDate-day');
    }
    set transferDateDay(value: string) {
      this.setInputValue('#transferDate-day', value);
    }

    get transferDateMonth() {
      return this.getInputValue('#transferDate-month');
    }
    set transferDateMonth(value: string) {
      this.setInputValue('#transferDate-month', value);
    }

    get transferDateYear() {
      return this.getInputValue('#transferDate-year');
    }
    set transferDateYear(value: string) {
      this.setInputValue('#transferDate-year', value);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransferADateComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      imports: [PermitTransferAModule, RouterTestingModule],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockState });
    fixture = TestBed.createComponent(TransferADateComponent);
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

  it('should form when stop date exists, not submit the form and navigate to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.transferDateDay.trim()).toEqual('16');
    expect(page.transferDateMonth.trim()).toEqual('11');
    expect(page.transferDateYear.trim()).toEqual('2022');

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'payment'], { relativeTo: route });
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
  });

  it('should submit form and navigate to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.transferDateDay = '24';
    page.transferDateMonth = '12';
    page.transferDateYear = `2022`;

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'payment'], { relativeTo: route });
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild({ transferDate: new Date('2022-12-24') }),
    );
  });
});
