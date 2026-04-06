import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage, RouterStubComponent } from '@testing';

import { BulkDownloadsService } from '../core/bulk-downloads.service';
import { Bdrs2BulkDownloadsComponent } from './bdrs2.component';

describe('Bdrs2BulkDownloadsComponent (ngOnInit behavior)', () => {
  let fixture: ComponentFixture<Bdrs2BulkDownloadsComponent>;
  let component: Bdrs2BulkDownloadsComponent;
  let page: Page;

  const periods = ['2021', '2020'];

  const mockBulkDownloadsService = {
    getBulkDownloadPeriods: jest.fn().mockReturnValue(of(periods)),
    getStreamingBulkDownloadUrl: jest.fn((type: string, period: string) => ({ downloadUrl: `url-for-${period}` })),
  };

  class Page extends BasePage<Bdrs2BulkDownloadsComponent> {
    get rows() {
      return this.queryAll<HTMLTableRowElement>('table tbody tr');
    }
    get rowTexts() {
      return this.rows.map((r) => r.textContent?.trim() ?? '');
    }
    get links() {
      return this.queryAll<HTMLAnchorElement>('a').map((a) => a.getAttribute('href'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, Bdrs2BulkDownloadsComponent],
      providers: [
        provideRouter([{ path: 'bdrd3', component: RouterStubComponent }]),
        { provide: BulkDownloadsService, useValue: mockBulkDownloadsService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Bdrs2BulkDownloadsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('maps periods to objects with downloadUrl', (done) => {
    component.periods$.subscribe((items: any[]) => {
      expect(items.length).toBe(2);
      expect(items[0]).toEqual({ period: '2021', downloadUrl: 'url-for-2021' });
      expect(items[1]).toEqual({ period: '2020', downloadUrl: 'url-for-2020' });
      expect(mockBulkDownloadsService.getStreamingBulkDownloadUrl).toHaveBeenCalled();
      done();
    });
  });

  it('renders periods and download links in the template', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    // rows should contain the download link titles (numeric and word forms)
    expect(page.rowTexts.some((t) => t.includes('2020 download'))).toBeTruthy();
    expect(page.rowTexts.some((t) => t.includes('2021 download'))).toBeTruthy();

    // links should include the download urls returned by the service
    expect(page.links).toEqual(expect.arrayContaining(['/url-for-2021', '/url-for-2020']));
  });
});
