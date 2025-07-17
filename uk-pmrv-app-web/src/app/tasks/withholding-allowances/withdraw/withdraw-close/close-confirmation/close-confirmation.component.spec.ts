import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { mockState } from '../../testing/mock-state';
import { CloseConfirmationComponent } from './close-confirmation.component';

describe('CloseConfirmationComponent', () => {
  let component: CloseConfirmationComponent;
  let fixture: ComponentFixture<CloseConfirmationComponent>;
  let page: Page;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ id: 500 }, null, null);

  class Page extends BasePage<CloseConfirmationComponent> {
    get confirmationMessage() {
      return this.query('.govuk-panel__title').innerHTML.trim();
    }

    get heading3(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h3');
    }

    get paragraph(): HTMLParagraphElement {
      return this.query<HTMLParagraphElement>('p.govuk-body');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }

    get panelBody() {
      return this.query<HTMLElement>('div.govuk-panel__body');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CloseConfirmationComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();

    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CloseConfirmationComponent],
      imports: [SharedModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CloseConfirmationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  describe('for new withholding of allowances', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show confirmation message', () => {
      expect(page.confirmationMessage).toBe('Task closed successfully');
      expect(page.panelBody.textContent.trim()).toEqual('Your reference code is: WA00025-1');
      expect(page.heading3.textContent.trim()).toEqual('What happens next');
      expect(page.paragraph.textContent.trim()).toEqual("You have marked this task as 'closed'.");
    });
  });
});
