package scottf;

import io.nats.client.Message;
import io.nats.client.ReadListener;

public class DebugReadListener implements ReadListener {
    String label;
    boolean proto;
    boolean regular;
    boolean js;

    public DebugReadListener() {
        this(null, true, true, true);
    }

    public DebugReadListener(String label) {
        this(label, true, true, true);
    }

    public DebugReadListener(boolean proto, boolean regular, boolean js) {
        this(null, proto, regular, js);
    }

    public DebugReadListener(String label, boolean proto, boolean regular, boolean js) {
        label(label);
        this.proto = proto;
        this.regular = regular;
        this.js = js;
    }

    public void label(String rlLabel) {
        this.label = rlLabel == null ? "RL" : rlLabel;
    }

    @Override
    public void protocol(String op, String text) {
        if (proto) {
            Debug.info(label + "/P", op, text);
        }
    }

    @Override
    public void message(String op, Message message) {
        if (regular && !message.isJetStream()) {
            Debug.msg(label + "/M", message);
        }
        else if (js && message.isJetStream()) {
            Debug.msg(label + "/JsM", message);
        }
    }
}
