import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@testing';

import { FileAttachmentsService, RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { testSchedulerFactory } from '../../../testing/marble-helpers';
import { SharedModule } from '../shared.module';
import { FILE_DOWNLOAD_SCHEDULER, FileDownloadComponent } from './file-download.component';

describe('FileDownloadComponent', () => {
  let component: FileDownloadComponent;
  let fixture: ComponentFixture<FileDownloadComponent>;
  let requestTaskAttachmentsHandlingService: jest.Mocked<RequestTaskAttachmentsHandlingService>;

  beforeEach(async () => {
    Object.defineProperty(window, 'onfocus', { set: jest.fn() });
    requestTaskAttachmentsHandlingService = mockClass(RequestTaskAttachmentsHandlingService);
    requestTaskAttachmentsHandlingService.generateRequestTaskGetFileAttachmentToken.mockReturnValue(
      of({ token: 'abce', tokenExpirationMinutes: 1 }),
    );
    const activatedRoute = new ActivatedRouteStub({ uuid: 'xyz', taskId: 11 });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: RequestTaskAttachmentsHandlingService, useValue: requestTaskAttachmentsHandlingService },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: FileAttachmentsService, useValue: { configuration: { basePath: '' } } },
        { provide: FILE_DOWNLOAD_SCHEDULER, useValue: testSchedulerFactory() },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    await expect(firstValueFrom(component.url$)).resolves.toEqual('/v1.0/file-attachments/abce');
  });
});
