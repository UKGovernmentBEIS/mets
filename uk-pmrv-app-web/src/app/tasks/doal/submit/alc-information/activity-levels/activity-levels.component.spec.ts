import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { mockDoalApplicationSubmitRequestTaskItem } from '../../../test/mock';
import { DoalActivityLevelsComponent } from './activity-levels.component';

describe('ActivityLevelsComponent', () => {
  let component: DoalActivityLevelsComponent;
  let fixture: ComponentFixture<DoalActivityLevelsComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<DoalActivityLevelsComponent> {
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

  function createComponent() {
    fixture = TestBed.createComponent(DoalActivityLevelsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DoalActivityLevelsComponent, DoalTaskComponent],
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

  it('should display historical data', () => {
    expect(page.historicalActivityLevelData).toEqual([
      [
        '2026',
        'District heating',
        'Authority rejects adjustment',
        'changedActivityLevel',
        'activityLevel1Comment',
        '29 Nov 2022',
      ],
    ]);
  });

  it('should display activity level data', () => {
    expect(page.activityLevelData).toEqual([
      ['2025', 'Adipic acid', 'Increase', 'changedActivityLevel', 'activityLevel1Comment', 'Change', 'Delete'],
    ]);
  });
});
