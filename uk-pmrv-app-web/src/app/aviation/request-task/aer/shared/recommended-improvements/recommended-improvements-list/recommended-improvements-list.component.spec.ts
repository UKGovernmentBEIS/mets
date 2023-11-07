import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';
import produce from 'immer';

import { RecommendedImprovementsFormProvider } from '../recommended-improvements-form.provider';
import { RecommendedImprovementsListComponent } from './recommended-improvements-list.component';

describe('RecommendedImprovementsListComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: RecommendedImprovementsListComponent;
  let fixture: ComponentFixture<RecommendedImprovementsListComponent>;

  class Page extends BasePage<RecommendedImprovementsListComponent> {
    get buttons() {
      return this.queryAll<HTMLButtonElement>('.govuk-button-group');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(RecommendedImprovementsListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecommendedImprovementsListComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: RecommendedImprovementsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.isEditable = true;
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              verificationReport: {
                recommendedImprovements: {
                  exist: true,
                  recommendedImprovements: [{ reference: 'reference', explanation: 'explanation' }],
                },
              },
            } as any,
          },
        };
      }),
    );
  });

  describe('for existing recommended Improvements', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the list', () => {
      expect(page.buttons).toHaveLength(2);
      expect(page.buttons.map((el) => el.textContent.trim())).toEqual(['Add another item', 'Continue']);
    });
  });
});
