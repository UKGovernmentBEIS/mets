import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { defer, firstValueFrom, of, take } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, mockClass } from '@testing';

import { AccountNotesService, FileNotesService } from 'pmrv-api';

import { testSchedulerFactory } from '../../../../testing/marble-helpers';
import { AccountsModule } from '../../../accounts/accounts.module';
import { NoteFileDownloadComponent } from './note-file-download.component';

describe('NoteFileDownloadComponent', () => {
  let component: NoteFileDownloadComponent;
  let fixture: ComponentFixture<NoteFileDownloadComponent>;
  let accountNotesService: jest.Mocked<AccountNotesService>;

  beforeEach(async () => {
    Object.defineProperty(window, 'onfocus', { set: jest.fn() });
    accountNotesService = mockClass(AccountNotesService);
    accountNotesService.generateGetAccountFileNoteToken.mockReturnValue(
      of({ token: 'abce', tokenExpirationMinutes: 1 }),
    );
    const activatedRoute = new ActivatedRouteStub({ accountId: 11 });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, AccountsModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: AccountNotesService, useValue: accountNotesService },
        { provide: FileNotesService, useValue: { configuration: { basePath: '' } } },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NoteFileDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    await expect(firstValueFrom(component.url$)).resolves.toEqual('/v1.0/file-notes/abce');
  });

  it('should refresh the download link', async () => {
    accountNotesService.generateGetAccountFileNoteToken.mockClear().mockImplementation(() => {
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
        a: '/v1.0/file-notes/abcf',
        b: '/v1.0/file-notes/abcd',
        c: '/v1.0/file-notes/abce',
      }),
    );
  });
});
