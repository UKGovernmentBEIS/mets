import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EmissionPointsTableComponent } from '@shared/components/emission-points/emission-points-table/emission-points-table.component';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { EmissionPointsComponent } from './emission-points.component';

describe('EmissionPointsComponent', () => {
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: EmissionPointsComponent;
  let fixture: ComponentFixture<EmissionPointsComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<EmissionPointsComponent> {
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button[type="button"]').find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addEmissionPointBtn() {
      return this.queryAll<HTMLButtonElement>('button[type="button"]').find(
        (button) => button.textContent.trim() === 'Add an emission point',
      );
    }

    get addAnotherEmissionPointBtn() {
      return this.queryAll<HTMLButtonElement>('button[type="button"]').find(
        (button) => button.textContent.trim() === 'Add another emission point',
      );
    }

    get emissionPoints() {
      return this.queryAll<HTMLDListElement>('tr');
    }

    get emissionPointsTextContents() {
      return this.emissionPoints.map((emissionPoint) =>
        Array.from(emissionPoint.querySelectorAll('td')).map((td) => td.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionPointsComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, EmissionPointsTableComponent],
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
    fixture = TestBed.createComponent(EmissionPointsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  describe('for adding new emission point', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new emission point button and hide complete button', () => {
      expect(page.submitButton).toBeFalsy();
      expect(page.addEmissionPointBtn).toBeFalsy();
      expect(page.addAnotherEmissionPointBtn).toBeFalsy();
      expect(page.emissionPoints).toHaveLength(0);

      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...store.getState(),
        isEditable: true,
      });
      fixture.detectChanges();

      expect(page.addEmissionPointBtn).toBeTruthy();
    });
  });

  describe('for existing emission points', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(mockState);
      fixture.detectChanges();
    });

    it('should show add another emission point button and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addEmissionPointBtn).toBeFalsy();
      expect(page.addAnotherEmissionPointBtn).toBeTruthy();
      expect(page.emissionPoints).toHaveLength(3);
    });

    it('should display the emission points', () => {
      expect(page.emissionPointsTextContents).toEqual([
        [],
        ['The big Ref', 'Emission point 1', 'Change', 'Delete'],
        ['Yet another reference', 'Point taken!', 'Change', 'Delete'],
      ]);
    });

    it('should submit the emission point task and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
      expect(store.payload.permitSectionsCompleted.emissionPoints).toEqual([true]);
      expect(store.permit.emissionPoints).toEqual(mockPermitApplyPayload.permit.emissionPoints);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(undefined, { emissionPoints: [true] }),
      );
    });
  });
});
