// Copyright 2021 The NATS Authors
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package scottf;

import io.nats.client.*;
import io.nats.client.support.Status;

public class DebugListener implements ErrorListener, ConnectionListener, ReadListener {
    String rlLabel;
    String clLabel;
    String elLabel;
    boolean printStackTrace;

    public DebugListener() {
        this("DbgRL", "DbgCL", "DbgEL");
    }

    public DebugListener(String rlLabel, String clLabel, String elLabel) {
        this.rlLabel = rlLabel;
        this.clLabel = clLabel;
        this.elLabel = elLabel;
    }

    public DebugListener rlLabel(String rlLabel) {
        this.rlLabel = rlLabel;
        return this;
    }

    public DebugListener clLabel(String clLabel) {
        this.clLabel = clLabel;
        return this;
    }

    public DebugListener elLabel(String elLabel) {
        this.elLabel = elLabel;
        return this;
    }

    public DebugListener printStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
        return this;
    }

    private String string(Connection conn) {
        return "Connection(" + conn.hashCode() + ") " + conn.getStatus();
    }

    @Override
    public void protocol(String op, String string) {
        if (rlLabel != null) {
            Debug.info(rlLabel + "-P", op, string);
        }
    }

    @Override
    public void message(String op, Message message) {
        if (rlLabel != null) {
            Debug.msg(rlLabel + "-M", message);
        }
    }

    @Override
    public void connectionEvent(Connection conn, Events type) {
        if (clLabel != null) {
            Debug.info(clLabel, string(conn), "Event: {}", type.getEvent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void errorOccurred(final Connection conn, final String error) {
        if (elLabel != null) {
            Debug.info(elLabel, "errorOccurred", string(conn), "Error: " + error);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void exceptionOccurred(final Connection conn, final Exception exp) {
        if (elLabel != null) {
            Debug.info(elLabel, "exceptionOccurred:", string(conn), exp);
            if (exp.getCause() != null) {
                Debug.info(elLabel, "            cause:", exp.getCause());
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
        if (elLabel != null) {
            Debug.info(elLabel, "slowConsumerDetected", string(conn), consumer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageDiscarded(final Connection conn, final Message msg) {
        if (elLabel != null) {
            Debug.info(elLabel, "messageDiscarded", string(conn), "Message: " + msg);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void heartbeatAlarm(final Connection conn, final JetStreamSubscription sub,
                               final long lastStreamSequence, final long lastConsumerSequence) {
        if (elLabel != null) {
            Debug.info(elLabel, "heartbeatAlarm", string(conn), sub, "lastStreamSequence: " + lastStreamSequence, "lastConsumerSequence: " + lastConsumerSequence);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unhandledStatus(final Connection conn, final JetStreamSubscription sub, final Status status) {
        if (elLabel != null) {
            Debug.info(elLabel, "unhandledStatus", string(conn), sub, "Status: " + status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pullStatusWarning(Connection conn, JetStreamSubscription sub, Status status) {
        if (elLabel != null) {
            Debug.info(elLabel, "pullStatusWarning", string(conn), sub, "Status: " + status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pullStatusError(Connection conn, JetStreamSubscription sub, Status status) {
        if (elLabel != null) {
            Debug.info(elLabel, "pullStatusError", string(conn), sub, "Status: " + status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flowControlProcessed(Connection conn, JetStreamSubscription sub, String id, FlowControlSource source) {
        if (elLabel != null) {
            Debug.info(elLabel, "flowControlProcessed", string(conn), sub, "FlowControlSource: " + source);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void socketWriteTimeout(Connection conn) {
        if (elLabel != null) {
            Debug.info(elLabel, "socketWriteTimeout", string(conn));
        }
    }
}
