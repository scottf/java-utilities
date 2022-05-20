package scottf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CancellableCountDownLatch {
    private final AtomicInteger count;
    private final CountDownLatch cdl;

    public CancellableCountDownLatch(int count) {
        this.count = new AtomicInteger(count);
        cdl = new CountDownLatch(1);
    }

    public void cancel() {
        count.set(0);
        cdl.countDown();
    }

    public void await() throws InterruptedException {
        cdl.await();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return cdl.await(timeout, unit);
    }

    public void countDown() {
        if (count.decrementAndGet() <= 0) {
            cdl.countDown();
        }
    }

    public long getCount() {
        return Math.max(count.get(), 0);
    }

    @Override
    public String toString() {
        return super.toString() + "[Count = " + getCount() + "]";
    }
}
