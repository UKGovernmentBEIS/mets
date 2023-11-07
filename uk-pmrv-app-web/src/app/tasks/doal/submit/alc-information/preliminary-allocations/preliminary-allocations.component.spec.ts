import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { mockDoalApplicationSubmitRequestTaskItem } from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { PreliminaryAllocationsComponent } from './preliminary-allocations.component';

describe('PreliminaryAllocationsComponent', () => {
  let component: PreliminaryAllocationsComponent;
  let fixture: ComponentFixture<PreliminaryAllocationsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<PreliminaryAllocationsComponent> {
    get data() {
      return this.queryAll<HTMLTableRowElement>('table tr')
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  function createComponent() {
    fixture = TestBed.createComponent(PreliminaryAllocationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
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
      requestTaskItem: mockDoalApplicationSubmitRequestTaskItem,
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display data', () => {
    expect(page.data).toEqual([['2025', 'Aluminium', '10', 'Change', 'Delete']]);
  });
});
