import { ChangeDetectionStrategy, Component, NgModule } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, firstValueFrom } from 'rxjs';

import { SharedModule } from '../../shared/shared.module';
import { BusinessError } from '../business-error/business-error';
import { BusinessErrorService } from '../business-error/business-error.service';

const clear = jest.fn();
const businessErrorService = { error$: new BehaviorSubject(null), clear };

export const expectBusinessErrorToBe = async (error: BusinessError) => {
  businessErrorService.error$.next(error);

  return expect(firstValueFrom(businessErrorService.error$)).resolves.toEqual(error);
};

@Component({ selector: 'app-business-error', template: '', changeDetection: ChangeDetectionStrategy.OnPush })
export class BusinessErrorStubComponent {}

@NgModule({
  imports: [
    RouterTestingModule.withRoutes([{ path: 'error/business', component: BusinessErrorStubComponent }]),
    SharedModule,
  ],
  declarations: [BusinessErrorStubComponent],
  providers: [{ provide: BusinessErrorService, useValue: businessErrorService }],
})
export class BusinessTestingModule {}
