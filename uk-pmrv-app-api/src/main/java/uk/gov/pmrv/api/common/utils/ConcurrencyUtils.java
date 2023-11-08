package uk.gov.pmrv.api.common.utils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@UtilityClass
@Log4j2
public class ConcurrencyUtils {

	public void completeCompletableFutures(CompletableFuture<?>... futures) {
		Arrays.stream(futures).forEach(future -> {
			if(future == null || future.isDone()) {
				return;
			}
			
			try {
				future.get();
			} catch (InterruptedException e) {
				Throwable caused = e.getCause();
				log.error(e.getMessage());
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
			} catch (ExecutionException e) {
				Throwable caused = e.getCause();
	            if (caused.getClass() == BusinessException.class) {
	                throw (BusinessException) caused;
	            } else {
	                log.error(caused.getMessage());
	                throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
	            }
			} catch (Exception e) {
				Throwable caused = e.getCause();
	            log.error(e.getMessage());
	            throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
			}
		});
	}
}
