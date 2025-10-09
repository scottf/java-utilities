package scottf;

import io.nats.client.*;
import io.nats.client.support.Status;

public class DebugErrorListener implements ErrorListener {
    String label;
    boolean printStackTrace;

    public DebugErrorListener() {
        this(null);
    }

    public DebugErrorListener(String label) {
        label(label);
    }

    public DebugErrorListener(String label, boolean printStackTrace) {
        this.label = label;
        this.printStackTrace = printStackTrace;
    }

    public void label(String elLabel) {
        this.label = elLabel == null ? "EL" : elLabel;
    }

    public void printStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
    }

    private String string(Connection conn) {
        return "Connection(" + conn.hashCode() + ") " + conn.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void errorOccurred(final Connection conn, final String error) {
        if (label != null) {
            Debug.info(label, "errorOccurred", string(conn), "Error: " + error);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void exceptionOccurred(final Connection conn, final Exception exp) {
        if (label != null) {
            Debug.info(label, "exceptionOccurred:", string(conn), exp);
            if (exp.getCause() != null) {
                Debug.info(label, "            cause:", exp.getCause());
                if (printStackTrace) {
                    exp.getCause().printStackTrace();
                }
            }
            else if (printStackTrace) {
                exp.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void slowConsumerDetected(final Connection conn, final Consumer consumer) {
        if (label != null) {
            Debug.info(label, "slowConsumerDetected", string(conn), consumer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageDiscarded(final Connection conn, final Message msg) {
        if (label != null) {
            Debug.info(label, "messageDiscarded", string(conn), "Message: " + msg);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void heartbeatAlarm(final Connection conn, final JetStreamSubscription sub,
                               final long lastStreamSequence, final long lastConsumerSequence) {
        if (label != null) {
            Debug.info(label, "heartbeatAlarm", string(conn), sub, "lastStreamSequence: " + lastStreamSequence, "lastConsumerSequence: " + lastConsumerSequence);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unhandledStatus(final Connection conn, final JetStreamSubscription sub, final Status status) {
        if (label != null) {
            Debug.info(label, "unhandledStatus", string(conn), sub, "Status: " + status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pullStatusWarning(Connection conn, JetStreamSubscription sub, Status status) {
        if (label != null) {
            Debug.info(label, "pullStatusWarning", string(conn), sub, "Status: " + status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pullStatusError(Connection conn, JetStreamSubscription sub, Status status) {
        if (label != null) {
            Debug.info(label, "pullStatusError", string(conn), sub, "Status: " + status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flowControlProcessed(Connection conn, JetStreamSubscription sub, String id, FlowControlSource source) {
        if (label != null) {
            Debug.info(label, "flowControlProcessed", string(conn), sub, "FlowControlSource: " + source);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void socketWriteTimeout(Connection conn) {
        if (label != null) {
            Debug.info(label, "socketWriteTimeout", string(conn));
        }
    }
}
