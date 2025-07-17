import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EmissionSourceTableComponent } from '@shared/components/emission-sources/emission-source-table/emission-source-table.component';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { EmissionSourcesComponent } from './emission-sources.component';

describe('EmissionSourcesComponent', () => {
  let component: EmissionSourcesComponent;
  let fixture: ComponentFixture<EmissionSourcesComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore<PermitApplicationState>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EmissionSourcesComponent> {
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addEmissionSourceBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add an emission source',
      );
    }

    get addAnotherEmissionSourceBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another emission source',
      );
    }

    get emissionSources() {
      return this.queryAll<HTMLDListElement>('tr');
    }

    get emissionSourcesTextContents() {
      return this.emissionSources.map((emissionSource) =>
        Array.from(emissionSource.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionSourcesComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, EmissionSourceTableComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(EmissionSourcesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new emission source', () => {
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new emission source button and hide complete button', () => {
      expect(page.submitButton).toBeFalsy();
      expect(page.addEmissionSourceBtn).toBeFalsy();
      expect(page.addAnotherEmissionSourceBtn).toBeFalsy();
      expect(page.emissionSources.length).toEqual(0);

      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...store.getState(),
        isEditable: true,
      });
      fixture.detectChanges();

      expect(page.addEmissionSourceBtn).toBeTruthy();
    });
  });

  describe('for existing emission sources', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should show add another emission source button and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addEmissionSourceBtn).toBeFalsy();
      expect(page.addAnotherEmissionSourceBtn).toBeTruthy();
      expect(page.emissionSources.length).toEqual(3);
    });

    it('should display the emission sources', () => {
      expect(page.emissionSourcesTextContents).toEqual([
        [],
        ['S1', 'Boiler', 'Change', 'Delete'],
        ['S2', 'Boiler 2', 'Change', 'Delete'],
      ]);
    });

    it('should submit the emission source task and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
      expect(store.payload.permitSectionsCompleted.emissionSources).toEqual([true]);
      expect(store.permit.emissionSources).toEqual(mockPermitApplyPayload.permit.emissionSources);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild({}, { emissionSources: [true] }),
      );
    });
  });
});
