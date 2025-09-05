import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { mockAlrAuthorityCompletedPayload, mockAlrAuthorityStateBuild } from '@tasks/alr/test/mock-authority';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { ALRAuthorityResponseSubmitRequestTaskPayload } from 'pmrv-api';

import { ALRPreliminaryAllocationsComponent } from './preliminary-allocations.component';

describe('ALRPreliminaryAllocationsComponent', () => {
  let component: ALRPreliminaryAllocationsComponent;
  let fixture: ComponentFixture<ALRPreliminaryAllocationsComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<ALRPreliminaryAllocationsComponent> {
    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  function createComponent() {
    fixture = TestBed.createComponent(ALRPreliminaryAllocationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([])],
      imports: [SharedModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrAuthorityStateBuild(mockAlrAuthorityCompletedPayload as ALRAuthorityResponseSubmitRequestTaskPayload),
    );
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display data', () => {
    expect(page.tableValues).toEqual([
      [],
      ['2023', 'Aluminium', '100', 'Change', 'Remove'],
      ['2024', 'Aluminium', '200', 'Change', 'Remove'],
    ]);
  });

  it('should submit and navigate to approved allocations', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../', 'approved-allocations'], { relativeTo: route });
  });
});
