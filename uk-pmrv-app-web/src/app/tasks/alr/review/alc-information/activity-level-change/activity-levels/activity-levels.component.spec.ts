import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AlrActivityLevelsComponent } from './activity-levels.component';

describe('ActivityLevelsComponent', () => {
  let component: AlrActivityLevelsComponent;
  let fixture: ComponentFixture<AlrActivityLevelsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<AlrActivityLevelsComponent> {
    get historicalActivityLevelData() {
      return this.getActivityLevelTable(0);
    }

    get activityLevelData() {
      return this.getActivityLevelTable(1);
    }

    private getActivityLevelTable(idx: number) {
      return Array.from(this.queryAll<HTMLTableRowElement>('table')[idx].querySelectorAll('tr'))
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrActivityLevelsComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockReviewState);

    fixture = TestBed.createComponent(AlrActivityLevelsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display historical data', () => {
    expect(page.historicalActivityLevelData).toEqual([
      ['2023', 'Adipic acid', 'Cessation', '15.55', 'comment', '2 Jul 2025'],
    ]);
  });

  it('should display activity level data', () => {
    expect(page.activityLevelData).toEqual([
      ['2022', 'Dolime', 'Regulator rejects adjustment', '11.55', 'Comments 1', 'Change', 'Delete'],
      ['2023', 'Facing bricks', 'Cessation', '43.33', 'Comments 2', 'Change', 'Delete'],
    ]);
  });
});
