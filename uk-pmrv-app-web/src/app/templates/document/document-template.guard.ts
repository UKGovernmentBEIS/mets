import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { DocumentTemplateDTO, DocumentTemplatesService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class DocumentTemplateGuard {
  documentTemplate: DocumentTemplateDTO;

  constructor(private readonly documentTemplatesService: DocumentTemplatesService) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.documentTemplatesService.getDocumentTemplateById(Number(route.paramMap.get('templateId'))).pipe(
      tap((documentTemplate) => (this.documentTemplate = documentTemplate)),
      map((documentTemplate) => !!documentTemplate),
    );
  }

  resolve(): DocumentTemplateDTO {
    return this.documentTemplate;
  }
}
