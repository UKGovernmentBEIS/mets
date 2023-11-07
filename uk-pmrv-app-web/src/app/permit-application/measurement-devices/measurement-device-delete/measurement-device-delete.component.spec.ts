import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { MeasurementDeviceDeleteComponent } from './measurement-device-delete.component';

describe('MeasurementDeviceDeleteComponent', () => {
  let component: MeasurementDeviceDeleteComponent;
  let fixture: ComponentFixture<MeasurementDeviceDeleteComponent>;
  let store: PermitApplicationStore<PermitApplicationState>;
  let router: Router;
  let page: Page;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({
    deviceId: mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].id,
  });

  class Page extends BasePage<MeasurementDeviceDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeasurementDeviceDeleteComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(MeasurementDeviceDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the measurement device name', () => {
    expect(page.header.textContent.trim()).toContain(
      mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].reference,
    );
  });

  it('should delete the measurement device', () => {
    expect(store.permit.measurementDevicesOrMethods).toEqual(mockPermitApplyPayload.permit.measurementDevicesOrMethods);

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const expectedMeasurementDevices = mockPermitApplyPayload.permit.measurementDevicesOrMethods.filter(
      (device) => device.id !== mockPermitApplyPayload.permit.measurementDevicesOrMethods[0].id,
    );

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        { measurementDevicesOrMethods: expectedMeasurementDevices },
        { measurementDevicesOrMethods: [false] },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(store.permit.measurementDevicesOrMethods).toEqual(expectedMeasurementDevices);
  });
});
