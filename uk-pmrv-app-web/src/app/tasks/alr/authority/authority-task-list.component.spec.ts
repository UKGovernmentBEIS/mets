import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AlrService } from '../core';
import { alrMockAuthorityState } from '../test/mock-authority';
import { AlrAuthorityTaskListComponent } from './authority-task-list.component';

describe('AuthorityTaskListComponent', () => {
  let component: AlrAuthorityTaskListComponent;
  let fixture: ComponentFixture<AlrAuthorityTaskListComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<AlrAuthorityTaskListComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get operatorText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(1)');
    }

    get daysRemainingText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(2)');
    }

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrAuthorityTaskListComponent],
      providers: [AlrService, CapitalizeFirstPipe, provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockAuthorityState);

    fixture = TestBed.createComponent(AlrAuthorityTaskListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Provide UK ETS Authority response for activity level change');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Regulator England');
    expect(page.daysRemainingText.textContent.trim()).toEqual('Days Remaining: Overdue');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Provide the date application was submitted to UK authorities',
      'Provide UK ETS Authority response',
      'Provide the latest activity level report',
    ]);
  });
});
