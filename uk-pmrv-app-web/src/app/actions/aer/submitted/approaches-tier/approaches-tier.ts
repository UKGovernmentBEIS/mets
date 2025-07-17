import { SourceStreamDescriptionPipe } from '../../../../shared/pipes/source-streams-description.pipe';

export function getCalculationHeading(payload, index, taskKey) {
  const sourceStreamEmission = payload.aer.monitoringApproachEmissions?.[taskKey]?.sourceStreamEmissions[index];
  const sourceStreamDescriptionPipe = new SourceStreamDescriptionPipe();
  const aerSourceStream = payload.aer.sourceStreams.find(
    (sourceStream) => sourceStream.id === sourceStreamEmission.sourceStream,
  );

  return `${aerSourceStream.reference} ${sourceStreamDescriptionPipe.transform(aerSourceStream.description)}`;
}

export function getMeasurementHeading(payload, index, taskKey) {
  const emissionPointEmission = payload.aer.monitoringApproachEmissions?.[taskKey]?.emissionPointEmissions[index];
  const aerEmissionPoint = payload.aer.emissionPoints.find(
    (emissionPoint) => emissionPoint.id === emissionPointEmission.emissionPoint,
  );

  return `${aerEmissionPoint.reference}`;
}
