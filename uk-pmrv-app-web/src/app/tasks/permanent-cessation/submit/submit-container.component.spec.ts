import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';

import { RequestTaskPayload } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { initialState } from '../../store/common-tasks.state';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { PermanentCessationService } from '../shared';
import { SubmitContainerComponent } from './submit-container.component';
import { mockPermanentCessationState } from './testing/mock-permanent-cessation-payload';

describe('SubmitContainerComponent', () => {
  let component: SubmitContainerComponent;
  let fixture: ComponentFixture<SubmitContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const activatedRoute = new ActivatedRouteStub({ taskId: 1 });

  class Page extends BasePage<SubmitContainerComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item'));
    }

    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get waitText() {
      return this.query<HTMLDivElement>('govuk-warning-text div').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [ItemNamePipe, PermanentCessationService, { provide: ActivatedRoute, useValue: activatedRoute }],
      imports: [SubmitContainerComponent],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(SubmitContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        ...mockPermanentCessationState.requestTaskItem,
        requestTask: {
          ...mockPermanentCessationState.requestTaskItem.requestTask,
          payload: {
            ...mockPermanentCessationState.requestTaskItem.requestTask.payload,
            permanentCessationSectionsCompleted: {},
          } as unknown as RequestTaskPayload,
        },
      },
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display header', () => {
    expect(page.heading.textContent.trim()).toEqual('Permanent cessation');
  });

  it('should display return of permanent cessation details section as not started', () => {
    expect(page.sections.length).toEqual(1);

    const link = page.sections[0].querySelector<HTMLAnchorElement>('.app-task-list__task-name a').textContent.trim();
    const status = page.sections[0].querySelector<HTMLElement>('.app-task-list__tag').textContent.trim();

    expect(link).toEqual('Permanent cessation details');
    expect(status).toEqual('not started');
  });

  it('should display return of permanent cessation wait for peer review content', () => {
    const state = store.getState();

    store.setState({
      ...state,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestTask: {
          ...state.requestTaskItem.requestTask,
          type: 'PERMANENT_CESSATION_WAIT_FOR_PEER_REVIEW',
        },
      },
    });

    fixture.detectChanges();

    expect(page.sections.length).toEqual(0);
    expect(page.waitText).toEqual('!WarningWaiting for peer review, you cannot make any changes');
  });
});
