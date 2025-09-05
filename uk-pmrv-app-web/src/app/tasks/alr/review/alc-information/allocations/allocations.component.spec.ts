import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AlrAllocationsComponent } from './allocations.component';

describe('AlrAllocationsComponent', () => {
  let component: AlrAllocationsComponent;
  let fixture: ComponentFixture<AlrAllocationsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<AlrAllocationsComponent> {
    get data() {
      return this.queryAll<HTMLTableRowElement>('table tr')
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  function createComponent() {
    fixture = TestBed.createComponent(AlrAllocationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
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
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          allocations: [{ subInstallationName: 'ALUMINIUM', year: 2025, allowances: 10 }],
        },
      }),
    );
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display data', () => {
    expect(page.data).toEqual([['2025', 'Aluminium', '10', 'Change', 'Remove']]);
  });
});
