import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MeasurementDevicesTableComponent } from '@permit-application/shared/measurement-devices-table/measurement-devices-table.component';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { MeasurementDevicesComponent } from './measurement-devices.component';

describe('MeasurementDevicesComponent', () => {
  let component: MeasurementDevicesComponent;
  let fixture: ComponentFixture<MeasurementDevicesComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore<PermitApplicationState>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<MeasurementDevicesComponent> {
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addMeasurementDeviceBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add a measurement device',
      );
    }
    get addAnotherMeasurementDeviceBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another measurement device',
      );
    }
    get measurementDevices() {
      return this.queryAll<HTMLDListElement>('tr');
    }
    get measurementDevicesTextContents() {
      return this.measurementDevices.map((measurementDevice) =>
        Array.from(measurementDevice.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeasurementDevicesComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, MeasurementDevicesTableComponent],
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
    fixture = TestBed.createComponent(MeasurementDevicesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding new measurement device', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(mockStateBuild({ measurementDevicesOrMethods: [] }));
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new measurement device button and hide complete button', () => {
      expect(page.submitButton).toBeFalsy();
      expect(page.addMeasurementDeviceBtn).toBeTruthy();
      expect(page.addAnotherMeasurementDeviceBtn).toBeFalsy();
      expect(page.measurementDevices.length).toEqual(0);
    });
  });

  describe('for existing measurement devices', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should show add another measurement device and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addMeasurementDeviceBtn).toBeFalsy();
      expect(page.addAnotherMeasurementDeviceBtn).toBeTruthy();
      expect(page.measurementDevices.length).toEqual(3);
    });

    it('should display the measurement devices', () => {
      expect(page.measurementDevicesTextContents).toEqual([
        [],
        ['ref1', 'Ultrasonic meter', '3', 'litres', '± 2.0 %', 'north terminal', 'Change', 'Delete'],
        ['ref2', 'Ultrasonic meter', '3', 'litres', 'None', 'north terminal', 'Change', 'Delete'],
      ]);
    });

    it('should submit the measurement device and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({ data: { id: 'test' } }));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
      expect(store.payload.permitSectionsCompleted.measurementDevicesOrMethods).toEqual([true]);
      expect(store.permit.measurementDevicesOrMethods).toEqual(
        mockPermitApplyPayload.permit.measurementDevicesOrMethods,
      );
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild({}, { measurementDevicesOrMethods: [true] }),
      );
    });
  });
});
