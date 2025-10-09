package scottf;

import io.nats.client.Message;
import io.nats.client.ReadListener;

public class DebugReadListener implements ReadListener {
    String label;
    boolean protoOnly;

    public DebugReadListener() {
        this(null);
    }

    public DebugReadListener(boolean protoOnly) {
        label(label);
        this.protoOnly = protoOnly;
    }

    public DebugReadListener(String label) {
        label(label);
    }

    public void label(String rlLabel) {
        this.label = rlLabel == null ? "RL" : rlLabel;
    }

    public void protoOnly(boolean protoOnly) {
        this.protoOnly = protoOnly;
    }

    @Override
    public void protocol(String op, String string) {
        if (label != null) {
            Debug.info(label + "-P", op, string);
        }
    }

    @Override
    public void message(String op, Message message) {
        if (!protoOnly && label != null) {
            Debug.msg(label + "-M", message);
        }
    }
}
