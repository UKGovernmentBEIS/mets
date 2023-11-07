import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskPayload } from 'pmrv-api';

import { NonComplianceModule } from '../non-compliance.module';
import { mockCompletedNonComplianceConclusionRequestTaskItem } from '../test/mock';
import { ConclusionComponent } from './conclusion.component';

describe('ConclusionComponent', () => {
  let component: ConclusionComponent;
  let fixture: ComponentFixture<ConclusionComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<ConclusionComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item'));
    }
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConclusionComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  const route = new ActivatedRouteStub(null, null, {
    pageTitle: 'Provide conclusion of non-compliance',
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, NonComplianceModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ConclusionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }
  describe('for new conclusion', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          requestTask: {
            ...mockCompletedNonComplianceConclusionRequestTaskItem.requestTask,
            payload: {
              determinationCompleted: false,
            } as RequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display header', () => {
      expect(page.heading).toEqual('Provide conclusion of non-compliance');
    });

    it('should display conclusion section as not started', () => {
      expect(page.sections.length).toEqual(1);
      expect(
        page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim(),
      ).toEqual('Provide conclusion of non-compliance');
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'not started',
      );
    });
  });

  describe('for in progress payload', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockCompletedNonComplianceConclusionRequestTaskItem,
          requestTask: {
            ...mockCompletedNonComplianceConclusionRequestTaskItem.requestTask,
            payload: {
              ...mockCompletedNonComplianceConclusionRequestTaskItem.requestTask.payload,
              determinationCompleted: false,
            } as RequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should display conclusion section as in progress', () => {
      expect(page.sections.length).toEqual(1);
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'in progress',
      );
    });
  });

  describe('for completed payload', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: mockCompletedNonComplianceConclusionRequestTaskItem,
      });
    });

    beforeEach(createComponent);

    it('should display conclusion section as completed', () => {
      expect(page.sections.length).toEqual(1);
      expect(page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim()).toEqual(
        'completed',
      );
    });
  });
});
