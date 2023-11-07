import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { PreliminaryAllocationsComponent } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocations.component';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { mockDoalAuthorityResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('PreliminaryAllocationsComponent', () => {
  let component: PreliminaryAllocationsComponent;
  let fixture: ComponentFixture<PreliminaryAllocationsComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<PreliminaryAllocationsComponent> {
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
    fixture = TestBed.createComponent(PreliminaryAllocationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreliminaryAllocationsComponent, DoalTaskComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: mockDoalAuthorityResponseRequestTaskTaskItem,
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display data', () => {
    expect(page.tableValues).toEqual([
      [],
      ['2023', 'Aluminium', '100', 'Change', 'Delete'],
      ['2024', 'Aluminium', '200', 'Change', 'Delete'],
    ]);
  });

  it('should submit and navigate to approved allocations', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../', 'approved-allocations'], { relativeTo: route });
  });
});
