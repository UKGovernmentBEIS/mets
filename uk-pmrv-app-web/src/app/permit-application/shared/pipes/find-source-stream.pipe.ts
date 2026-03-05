import { Pipe, PipeTransform } from '@angular/core';

import { iif, map, Observable } from 'rxjs';

import { SourceStream } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Pipe({ name: 'findSourceStream' })
export class FindSourceStreamPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore<PermitApplicationState>) {}

  transform(sourceStream?: string, currentPermit = true): Observable<SourceStream> {
    return iif(
      () => currentPermit,
      this.store.getTask('sourceStreams'),
      this.store.getOriginalTask('sourceStreams'),
    ).pipe(map((sourceStreams) => sourceStreams.find((stream) => stream.id === sourceStream)));
  }
}
