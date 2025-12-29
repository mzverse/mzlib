package test;

import mz.mzlib.util.RuntimeUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.TestAbortedException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicInteger;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(RetryExtension.class)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface RetryOnFailure {
    int maxRetries() default 10;
    long delayMillis() default 100;
}

class RetryExtension implements TestExecutionExceptionHandler
{

    @Override
    public void handleTestExecutionException(
        ExtensionContext context, Throwable throwable)
        throws Throwable {

        RetryOnFailure retry = context.getTestMethod()
            .orElseGet(() -> RuntimeUtil.valueThrow(new RuntimeException("No test method found")))
            .getAnnotation(RetryOnFailure.class);

        if (retry != null) {
            AtomicInteger retryCount = getRetryCount(context);
            int maxRetries = retry.maxRetries();

            if (retryCount.get() < maxRetries) {
                retryCount.incrementAndGet();
                Thread.sleep(retry.delayMillis());
                throw new TestAbortedException(
                    String.format("重试 %d/%d", retryCount.get(), maxRetries),
                    throwable
                );
            }
        }
        throw throwable;
    }

    private AtomicInteger getRetryCount(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.GLOBAL)
            .getOrComputeIfAbsent("retryCount",
                k -> new AtomicInteger(0), AtomicInteger.class);
    }
}