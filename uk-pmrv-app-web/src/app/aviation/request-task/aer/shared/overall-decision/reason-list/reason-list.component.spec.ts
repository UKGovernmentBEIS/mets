import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';
import produce from 'immer';

import { OverallDecisionFormProvider } from '../overall-decision-form.provider';
import { ReasonListComponent } from './reason-list.component';

describe('ReasonListComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: ReasonListComponent;
  let fixture: ComponentFixture<ReasonListComponent>;

  class Page extends BasePage<ReasonListComponent> {
    get buttons() {
      return this.queryAll<HTMLButtonElement>('.govuk-button-group');
    }
    get pageBody() {
      return this.query<HTMLElement>('.govuk-body');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReasonListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonListComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OverallDecisionFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.isEditable = true;
        state.requestTaskItem = {
          requestInfo: {
            type: 'AVIATION_AER_UKETS',
          },
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              verificationReport: {
                overallDecision: {
                  type: 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS',
                  reasons: ['reason 1'],
                },
              },
            } as any,
          },
        };
      }),
    );
  });

  describe('for existing overall decision', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show body statement', () => {
      expect(page.pageBody.textContent.trim()).toEqual(
        'On the basis of your verification work these data are fairly stated, with the exception of the following reasons.',
      );
    });

    it('should show the list', () => {
      expect(page.buttons).toHaveLength(1);
      expect(page.buttons.map((el) => el.textContent.trim())).toEqual(['Continue']);
    });
  });
});
