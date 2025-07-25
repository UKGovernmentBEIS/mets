import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SourceStreamsTableComponent } from '@shared/components/source-streams/source-streams-table/source-streams-table.component';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { SourceStreamsComponent } from './source-streams.component';

describe('SourceStreamsComponent', () => {
  let component: SourceStreamsComponent;
  let fixture: ComponentFixture<SourceStreamsComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore<PermitApplicationState>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SourceStreamsComponent> {
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addSourceStreamBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add a source stream',
      );
    }

    get addAnotherSourceStreamBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another source stream',
      );
    }

    get sourceStreams() {
      return this.queryAll<HTMLDListElement>('tr');
    }

    get sourceStreamsTextContents() {
      return this.sourceStreams.map((sourceStream) =>
        Array.from(sourceStream.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SourceStreamsComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, SourceStreamsTableComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SourceStreamsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  describe('for adding new source stream', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new stream button and hide complete button', () => {
      expect(page.submitButton).toBeFalsy();
      expect(page.addSourceStreamBtn).toBeFalsy();
      expect(page.addAnotherSourceStreamBtn).toBeFalsy();
      expect(page.sourceStreams.length).toEqual(0);

      const store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...store.getState(),
        isEditable: true,
      });
      fixture.detectChanges();

      expect(page.addSourceStreamBtn).toBeTruthy();
    });
  });

  describe('for existing source streams', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(mockState);
      fixture.detectChanges();
    });

    it('should show add another stream button and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addSourceStreamBtn).toBeFalsy();
      expect(page.addAnotherSourceStreamBtn).toBeTruthy();
      expect(page.sourceStreams.length).toEqual(3);
    });

    it('should display the source streams', () => {
      expect(page.sourceStreamsTextContents).toEqual([
        [],
        ['13123124', 'White Spirit & SBP', 'Refineries: Hydrogen production', 'Change', 'Delete'],
        ['33334', 'Lignite', 'Refineries: Catalytic cracker regeneration', 'Change', 'Delete'],
      ]);
    });

    it('should submit the source streams, complete the task and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
      expect(store.payload.permitSectionsCompleted.sourceStreams).toEqual([true]);
      expect(store.permit.sourceStreams).toEqual(mockPermitApplyPayload.permit.sourceStreams);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockPostBuild({}, { sourceStreams: [true] }));
    });
  });
});
