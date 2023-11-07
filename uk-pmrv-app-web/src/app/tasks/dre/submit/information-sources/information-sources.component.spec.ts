import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { DreTaskComponent } from '../../shared/components/dre-task/dre-task.component';
import { updateMockedDre } from '../../test/mock';
import { InformationSourcesComponent } from './information-sources.component';

describe('InformationSourcesComponent', () => {
  let component: InformationSourcesComponent;
  let fixture: ComponentFixture<InformationSourcesComponent>;

  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<InformationSourcesComponent> {
    get itemListValues() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get addButton() {
      return this.query<HTMLButtonElement>('a[type="button"]');
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(InformationSourcesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InformationSourcesComponent, DreTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new dre', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockedDre({ informationSources: [] }, false),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should navigate to add item when add item button clicked', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.addButton.textContent.trim()).toEqual('Add an item');
      expect(page.submitButton).toBeNull();
    });
  });

  describe('for existing dre', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockedDre({ informationSources: ['item1', 'item2'] }, false),
      });
    });
    beforeEach(createComponent);

    it('should navigate to add item when add item button clicked', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.addButton.textContent.trim()).toEqual('Add another item');
      expect(page.submitButton).not.toBeNull();
      expect(page.itemListValues.map((el) => el.textContent.trim())).toEqual(['item1', 'item2']);
    });
  });
});
