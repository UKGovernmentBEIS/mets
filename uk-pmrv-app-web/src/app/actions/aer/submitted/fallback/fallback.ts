import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

export function getFallbackSourceStreams(payload, fallbackEmissions) {
  const sourceStreamDescriptionPipe = new SourceStreamDescriptionPipe();
  return fallbackEmissions?.sourceStreams.map((sourceStreamKey) => {
    const aerSourceStream = payload.aer.sourceStreams.find((sourceStream) => sourceStream.id === sourceStreamKey);

    return `${aerSourceStream.reference} ${sourceStreamDescriptionPipe.transform(aerSourceStream.description)}`;
  });
}
