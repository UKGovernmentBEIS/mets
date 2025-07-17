import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { defer, firstValueFrom, of, take } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, mockClass } from '@testing';

import { DocumentTemplateFilesService, FileDocumentTemplatesService, TasksService } from 'pmrv-api';

import { testSchedulerFactory } from '../../../testing/marble-helpers';
import { TemplatesModule } from '../templates.module';
import { TemplateFileDownloadComponent } from './template-file-download.component';

describe('TemplateFileDownloadComponent', () => {
  let component: TemplateFileDownloadComponent;
  let fixture: ComponentFixture<TemplateFileDownloadComponent>;
  let documentTemplateFilesService: jest.Mocked<DocumentTemplateFilesService>;

  beforeEach(async () => {
    Object.defineProperty(window, 'onfocus', { set: jest.fn() });
    documentTemplateFilesService = mockClass(DocumentTemplateFilesService);
    documentTemplateFilesService.generateGetDocumentTemplateFileToken.mockReturnValue(
      of({ token: 'abce', tokenExpirationMinutes: 1 }),
    );
    const activatedRoute = new ActivatedRouteStub({ templateId: 11 });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, TemplatesModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: DocumentTemplateFilesService, useValue: documentTemplateFilesService },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: FileDocumentTemplatesService, useValue: { configuration: { basePath: '' } } },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TemplateFileDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    await expect(firstValueFrom(component.url$)).resolves.toEqual('/v1.0/file-document-templates/abce');
  });

  it('should refresh the download link', async () => {
    documentTemplateFilesService.generateGetDocumentTemplateFileToken.mockClear().mockImplementation(() => {
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
        a: '/v1.0/file-document-templates/abcf',
        b: '/v1.0/file-document-templates/abcd',
        c: '/v1.0/file-document-templates/abce',
      }),
    );
  });
});
