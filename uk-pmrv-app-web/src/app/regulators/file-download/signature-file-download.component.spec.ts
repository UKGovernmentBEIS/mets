import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { defer, firstValueFrom, of, take } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, mockClass } from '@testing';

import { RegulatorUsersService, TasksService, UsersService } from 'pmrv-api';

import { testSchedulerFactory } from '../../../testing/marble-helpers';
import { RegulatorsModule } from '../regulators.module';
import { SignatureFileDownloadComponent } from './signature-file-download.component';

describe('SignatureFileDownloadComponent', () => {
  let component: SignatureFileDownloadComponent;
  let fixture: ComponentFixture<SignatureFileDownloadComponent>;
  let regulatorUsersService: jest.Mocked<RegulatorUsersService>;

  beforeEach(async () => {
    Object.defineProperty(window, 'onfocus', { set: jest.fn() });
    regulatorUsersService = mockClass(RegulatorUsersService);
    regulatorUsersService.generateGetRegulatorSignatureToken.mockReturnValue(
      of({ token: 'abce', tokenExpirationMinutes: 1 }),
    );
    const activatedRoute = new ActivatedRouteStub({ userId: 11 });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, RegulatorsModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: RegulatorUsersService, useValue: regulatorUsersService },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: UsersService, useValue: { configuration: { basePath: '' } } },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SignatureFileDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    await expect(firstValueFrom(component.url$)).resolves.toEqual('/v1.0/user-signatures/abce');
  });

  it('should refresh the download link', async () => {
    regulatorUsersService.generateGetRegulatorSignatureToken.mockClear().mockImplementation(() => {
      let subscribes = 0;

      return defer(() => {
        subscribes += 1;

        return subscribes === 1
          ? of({ token: 'abcf', tokenExpirationMinutes: 1 })
          : subscribes === 2
          ? of({ token: 'abcd', tokenExpirationMinutes: 2 })
          : of({ token: 'abce', tokenExpirationMinutes: 1 });
      });
    });

    testSchedulerFactory().run(({ expectObservable }) =>
      expectObservable(component.url$.pipe(take(3))).toBe('a 59s 999ms b 119s 999ms (c|)', {
        a: '/v1.0/user-signatures/abcf',
        b: '/v1.0/user-signatures/abcd',
        c: '/v1.0/user-signatures/abce',
      }),
    );
  });
});
