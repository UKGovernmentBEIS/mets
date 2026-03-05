import { Pipe, PipeTransform } from '@angular/core';

import { MeasurementDeviceOrMethod } from 'pmrv-api';

import { MeasurementDevicesTypePipe } from '../../measurement-devices/measurement-devices-summary/measurement-devices-type.pipe';

@Pipe({ name: 'measurementDeviceOrMethodName' })
export class MeasurementDeviceOrMethodNamePipe implements PipeTransform {
  constructor(private measurementDevicesTypePipe: MeasurementDevicesTypePipe) {}

  transform(measurementDevicesOrMethod: MeasurementDeviceOrMethod): string {
    const measurementDevicesTypeTransformation =
      measurementDevicesOrMethod.type === 'OTHER'
        ? measurementDevicesOrMethod.otherTypeName
        : this.measurementDevicesTypePipe.transform(measurementDevicesOrMethod.type);

    const name = `${measurementDevicesOrMethod.reference}, ${measurementDevicesTypeTransformation}`;

    return measurementDevicesOrMethod.uncertaintySpecified
      ? `${name}, Specified uncertainty \u00b1${measurementDevicesOrMethod.specifiedUncertaintyPercentage}%`
      : name;
  }
}
