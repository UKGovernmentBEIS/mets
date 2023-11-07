import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AuthorityResponseContainerComponent } from '@tasks/doal/authority-response/authority-response-container.component';
import { mockDoalAuthorityResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskPayload } from 'pmrv-api';

import { TaskSharedModule } from '../../shared/task-shared-module';
import { initialState } from '../../store/common-tasks.state';
import { CommonTasksStore } from '../../store/common-tasks.store';

describe('AuthorityResponseContainerComponent', () => {
  let component: AuthorityResponseContainerComponent;
  let fixture: ComponentFixture<AuthorityResponseContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<AuthorityResponseContainerComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item'));
    }

    get sectionStatuses(): HTMLElement[] {
      return this.sections.map((section) => section.querySelector<HTMLElement>('.app-task-list__tag'));
    }

    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AuthorityResponseContainerComponent],
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(AuthorityResponseContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  describe('for new doal authority', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockDoalAuthorityResponseRequestTaskTaskItem,
          requestTask: {
            ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask,
            payload: {
              ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.payload,
              doalAuthority: null,
              doalSectionsCompleted: {},
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
      expect(page.heading).toEqual('Provide UK ETS Authority response for activity Level Change');
    });

    it('should display sections as not started', () => {
      expect(page.sections.length).toEqual(2);
      expect(page.sectionStatuses.map((st) => st.textContent.trim())).toEqual(['not started', 'not started']);
    });
  });

  describe('for completed doal authority', () => {
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

    it('should display sections as completed', () => {
      expect(page.sectionStatuses.map((st) => st.textContent.trim())).toEqual(['completed', 'completed']);
    });
  });

  describe('for in progress doal authority', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockDoalAuthorityResponseRequestTaskTaskItem,
          requestTask: {
            ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask,
            payload: {
              ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.payload,
              doalAuthority: null,
              doalSectionsCompleted: {
                dateSubmittedToAuthority: false,
                authorityResponse: false,
              },
            } as RequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should display sections as in progress', () => {
      expect(page.sectionStatuses.map((st) => st.textContent.trim())).toEqual(['in progress', 'in progress']);
    });
  });
});
