import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { RouterStubComponent } from '../../../testing';
import { buildSaveNotFoundError } from './business-error';
import { BusinessErrorService } from './business-error.service';

describe('BusinessErrorService', () => {
  let service: BusinessErrorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'error/business', component: RouterStubComponent }])],
      declarations: [RouterStubComponent],
    });
    service = TestBed.inject(BusinessErrorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should show an error', async () => {
    await expect(firstValueFrom(service.error$)).resolves.toBeNull();

    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');
    const error = buildSaveNotFoundError().withLink({ linkText: 'Back', link: ['/'] });

    await expect(firstValueFrom(service.showError(error))).rejects.toBeTruthy();

    await expect(firstValueFrom(service.error$)).resolves.toEqual({
      heading: 'These changes cannot be saved because the information no longer exists',
      linkText: 'Back',
      link: ['/'],
    });
    expect(navigateSpy).toHaveBeenCalledWith(['/error/business'], { skipLocationChange: true });
  });

  it('should clear the error', async () => {
    service.showError(buildSaveNotFoundError().withLink({ linkText: 'Back', link: ['/'] }));
    service.clear();

    await expect(firstValueFrom(service.error$)).resolves.toBeNull();
  });
});
