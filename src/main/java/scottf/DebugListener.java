package scottf;

import io.nats.client.*;
import io.nats.client.support.Status;

public class DebugListener implements ErrorListener, ConnectionListener, ReadListener {
    DebugErrorListener el;
    DebugConnectionListener cl;
    DebugReadListener rl;

    public DebugListener() {
        this(null, null, null);
    }

    public DebugListener(String elLabel, String clLabel, String rlLabel) {
        el = new DebugErrorListener(elLabel);
        cl = new DebugConnectionListener(clLabel);
        rl = new DebugReadListener(rlLabel);
    }

    public void elLabel(String elLabel) {
        el.label(elLabel);
    }

    public void clLabel(String clLabel) {
        cl.label(clLabel);
    }

    public void rlLabel(String rlLabel) {
        rl.label(rlLabel);
    }

    public void elPrintStackTrace(boolean printStackTrace) {
        el.printStackTrace(printStackTrace);
    }

    @Override
    public void protocol(String op, String string) {
        rl.protocol(op, string);
    }

    @Override
    public void message(String op, Message message) {
        rl.message(op, message);
    }

    @Override
    public void connectionEvent(Connection conn, Events type) {
        cl.connectionEvent(conn, type);
    }

    @Override
    public void connectionEvent(Connection conn, Events type, Long time, String uriDetails) {
        cl.connectionEvent(conn, type, time, uriDetails);
    }

    @Override
    public void errorOccurred(final Connection conn, final String error) {
        el.errorOccurred(conn, error);
    }

    @Override
    public void exceptionOccurred(final Connection conn, final Exception exp) {
        el.exceptionOccurred(conn, exp);
    }

    @Override
    public void slowConsumerDetected(final Connection conn, final Consumer consumer) {
        el.slowConsumerDetected(conn, consumer);
    }

    @Override
    public void messageDiscarded(final Connection conn, final Message msg) {
        el.messageDiscarded(conn, msg);
    }

    @Override
    public void heartbeatAlarm(final Connection conn, final JetStreamSubscription sub,
                               final long lastStreamSequence, final long lastConsumerSequence)
    {
        el.heartbeatAlarm(conn, sub, lastStreamSequence, lastConsumerSequence);
    }

    @Override
    public void unhandledStatus(final Connection conn, final JetStreamSubscription sub, final Status status) {
        el.unhandledStatus(conn, sub, status);
    }

    @Override
    public void pullStatusWarning(Connection conn, JetStreamSubscription sub, Status status) {
        el.pullStatusWarning(conn, sub, status);
    }

    @Override
    public void pullStatusError(Connection conn, JetStreamSubscription sub, Status status) {
        el.pullStatusError(conn, sub, status);
    }

    @Override
    public void flowControlProcessed(Connection conn, JetStreamSubscription sub, String id, FlowControlSource source) {
        el.flowControlProcessed(conn, sub, id, source);
    }

    @Override
    public void socketWriteTimeout(Connection conn) {
        el.socketWriteTimeout(conn);
    }
}
