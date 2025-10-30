package scottf;

import io.nats.client.Connection;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.Message;
import io.nats.client.api.*;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsJetStreamMetaData;
import io.nats.client.impl.NatsMessage;
import io.nats.client.support.DateTimeUtils;
import io.nats.client.support.JsonSerializable;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.nats.client.support.DateTimeUtils.toRfc3339;
import static java.nio.charset.StandardCharsets.UTF_8;

@SuppressWarnings("SameParameterValue")
public abstract class Debug {

    public interface DebugPrinter {
        void println(String s);
    }

    public static final int NO_TIME = 0;
    public static final int SIMPLE_TIME = 1;
    public static final int RFC_DATE_TIME = 2;
    public static final int RFC_TIME = 3;
    public static final int MILLIS_TIME = 4;

    public static String SEP = " | ";
    public static String PART_SEP = " / ";
    public static String DIV = "/";
    public static String PAD = "                                                                                                                                                                                                                                                                                                                                                                                                                                    ";
    public static String REPLACE = "\\Q%s\\E";
    public static boolean DO_NOT_TRUNCATE = true;
    public static boolean PRINT_THREAD_ID = true;
    public static int TIME_TYPE = SIMPLE_TIME;
    public static String DEFAULT_MESSAGE_LABEL = "MSG";
    public static boolean PAUSE = false;
    public static DebugPrinter DEBUG_PRINTER = System.out::println;
    public static int MAX_DATA_DISPLAY = 50;

    private Debug() {}  /* ensures cannot be constructed */

    public static void msg(Message msg) {
        info(DEFAULT_MESSAGE_LABEL, msg, true, null);
    }

    public static void msg(Message msg, Object... extras) {
        info(DEFAULT_MESSAGE_LABEL, msg, true, extras, false);
    }

    public static void msg(String label, Message msg, Object... extras) {
        info(label, msg, true, extras, false);
    }

    public static Object[] combine(Object[] original, String... inserts) {
        Object[] combined = new Object[original.length + inserts.length];
        System.arraycopy(inserts, 0, combined, 0, inserts.length);
        System.arraycopy(original, 0, combined, inserts.length, original.length);
        return combined;
    }

    public static void stackTrace(String label, Object... extras) {
        if (PAUSE) { return; }
        try {
            throw new Exception();
        }
        catch (Exception e) {
            stackTrace(label, e, extras);
        }
    }
    public static void stackTrace(String label, Throwable t, Object... extras) {
        if (PAUSE) { return; }
        info(label, combine(extras, t.toString()));
        StackTraceElement[] elements = t.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            String ts = elements[i].toString();
            if (ts.contains("Debug.stackTrace")) {
                continue;
            }
            if (i > 0) {
                for (String stop : STACK_TRACE_STOPS) {
                    if (ts.startsWith(stop)) {
                        return;
                    }
                }

                boolean foundIgnore = false;
                for (String ignore : STACK_TRACE_IGNORES) {
                    if (ts.startsWith(ignore)) {
                        foundIgnore = true;
                        break;
                    }
                }
                if (foundIgnore) {
                    continue;
                }
            }
            info(label, ">  " + ts);
        }
    }

    public static String[] STACK_TRACE_STOPS = new String[]{"org.junit.", "com.intellij."};
    public static String[] STACK_TRACE_IGNORES = new String[]{"sun."};

    public static void info(String label, Object... extras) {
        if (PAUSE) { return; }
        if (extras == null || extras.length == 0) {
            info(label, null, false, null, false);
        }
        else if (extras[0] instanceof NatsMessage) {
            info(label, (NatsMessage)extras[0], true, extras, true);
        }
        else {
            info(label, null, false, extras, false);
        }
    }

    public static void info(String label, Message msg, boolean forMsg, Object[] extras, boolean skipFirst) {
        if (PAUSE) { return; }
        String start;
        if (TIME_TYPE > NO_TIME && PRINT_THREAD_ID) {
            start = "[" + getThreadName() + "@" + time(TIME_TYPE) + "] ";
        }
        else if (TIME_TYPE > NO_TIME) {
            start = "[" + time(TIME_TYPE) + "] ";
        }
        else if (PRINT_THREAD_ID){
            start = "[" + getThreadName() + "] ";
        }
        else {
            start = "";
        }

        if (label != null) {
            label = label.trim();
        }
        if (label == null || label.isEmpty()) {
            label = start;
        }
        else {
            label = start + label;
        }

        int indent = label.length() + 1;
        String extra = stringify(indent, extras, skipFirst);

        if (extra == null) {
            extra = "";
        }
        else {
            extra = SEP + extra;
        }

        if (msg == null) {
            if (forMsg) {
                DEBUG_PRINTER.println(label + "<nullmsg>" + extra);
            }
            else {
                DEBUG_PRINTER.println(label + extra);
            }
            return;
        }

        if (msg.getSubject() == null) {
            DEBUG_PRINTER.println(label + SEP + protocolMsgString(msg) + extra);
            return;
        }

        DEBUG_PRINTER.println(label + SEP + messageString(msg));
        debugHdr(indent, msg);
    }

    private static String getThreadName() {
        return Thread.currentThread().getName().replace("-thread-", "-");
    }

    private static String messageString(Message msg) {
        String sid = sidString(msg);
        sid = sid == null ? "" : sid + PART_SEP;

        if (msg.getSubject() == null) {
            return sid + protocolMsgString(msg);
        }

        if (msg.isStatusMessage()) {
            return sid + msgInfoString(msg) + PART_SEP + msg.getStatus();
        }

        return sid + msgInfoString(msg) + PART_SEP + dataString(msg) + PART_SEP + replyToString(msg);
    }

    public static String sidString(Message msg) {
        return msg.getSID() == null ? null : "sid:" + msg.getSID();
    }

    private static String protocolMsgString(Message msg) {
        String s = msg.toString();
        int at1 = s.indexOf('|');
        int at2 = s.indexOf(' ', at1 + 2);
        return at2 == -1 ? s : s.substring(0, at2);
    }

    public static String msgInfoString(Message msg) {
        if (msg.isJetStream()) {
            return msg.metaData().streamSequence()
                + DIV + msg.metaData().consumerSequence()
                + PART_SEP + msg.getSubject();
        }
        return msg.getSubject();
    }

    public static String dataString(Message msg) {
        byte[] data = msg.getData();
        if (data == null || data.length == 0) {
            return "<no data>";
        }

        if (data[0] < 32) {
            // this must be actual binary data, probably filler test data
            return "<binary " + data.length + " byte(s)>";
        }

        String s = new String(data, UTF_8);
        if (DO_NOT_TRUNCATE) {
            return s;
        }

        int at = s.indexOf("io.nats.jetstream.api");
        if (at == -1) {
            return s.length() > MAX_DATA_DISPLAY ? s.substring(0, MAX_DATA_DISPLAY) + "..." : s;
        }
        int at2 = s.indexOf('"', at);
        return s.substring(at, at2);
    }

    public static String replyToString(Message msg) {
        if (msg.isJetStream()) {
            NatsJetStreamMetaData meta = msg.metaData();
            return "ss:" + meta.streamSequence() + ' '
                + "cc:" + meta.consumerSequence() + ' '
                + "dlvr:" + meta.deliveredCount() + ' '
                + "pnd:" + meta.pendingCount();
        }
        return msg.getReplyTo() == null ? "<no reply>" : msg.getReplyTo();
    }

    public static String time() {
        return simpleTime();
    }

    public static String time(int type) {
        switch (type) {
            case RFC_DATE_TIME: return rfcDateTime();
            case RFC_TIME: return rfcTime();
            case MILLIS_TIME: return "" + System.currentTimeMillis();
            case SIMPLE_TIME: default: return simpleTime();
        }
    }

    // RFC 2025-02-15T14:09:45
    public static String rfcDateTime() {
        return toRfc3339(DateTimeUtils.gmtNow()).substring(0, 19);
    }

    public static String rfcDateTime(ZonedDateTime zdt) {
        return toRfc3339(zdt).substring(0, 19);
    }

    public static String rfcTime() {
        return rfcTime(DateTimeUtils.gmtNow());
    }

    public static String rfcTime(ZonedDateTime zdt) {
        return toRfc3339(zdt).substring(11);
    }

    public static final ZoneId ZONE_ID_GMT = ZoneId.of("GMT");
    public static final DateTimeFormatter SIMPLE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static String simpleTime(long javaTime) {
        return SIMPLE_TIME_FORMATTER.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(javaTime), ZONE_ID_GMT));
    }

    public static String simpleTime() {
        return SIMPLE_TIME_FORMATTER.format(DateTimeUtils.gmtNow());
    }

    public static String simpleTime(ZonedDateTime zdt) {
        return SIMPLE_TIME_FORMATTER.format(zdt);
    }

    public static String stringify(Object... extras) {
        return stringify(0, extras, false);
    }

    public static String stringify(int indent, Object[] extras, boolean skipFirst) {
        if (extras == null || extras.length == 0) {
            return null;
        }

        if (extras.length == 1) {
            return skipFirst || extras[0] == null ? null : getString(indent, extras[0]);
        }

        boolean notFirst = false;
        StringBuilder sb = new StringBuilder();
        for (int i = (skipFirst ? 1 : 0); i < extras.length; i++) {
            Object xi = extras[i];
            if (xi != null) {
                if (notFirst) {
                    sb.append(SEP);
                }
                else {
                    notFirst = true;
                }

                String xtra = getString(indent, extras[i]);
                while (xtra.contains("%s")) {
                    xtra = xtra.replaceFirst(REPLACE, getString(indent, extras[++i]));
                }
                sb.append(xtra);
            }
        }

        return sb.length() == 0 ? null : sb.toString();
    }

    public static String getString(Object o) {
        return getString(0, o);
    }

    public static String getString(int indent, Object o) {
        if (o == null) {
            return "null";
        }
        if (o instanceof Message) {
            return messageString((Message)o);
        }
        if (o instanceof ConsumerInfo) {
            return consumerInfoString((ConsumerInfo)o);
        }
        if (o instanceof SequenceInfo) {
            return sequenceInfoString((SequenceInfo)o);
        }
        if (o instanceof ServerInfo) {
            return serverInfoString(indent, (ServerInfo)o);
        }
        if (o instanceof NatsJetStreamMetaData) {
            return metaDataString((NatsJetStreamMetaData)o);
        }
        if (o instanceof ZonedDateTime) {
            return DateTimeUtils.toRfc3339((ZonedDateTime)o);
        }
        if (o instanceof ConsumerConfiguration) {
            return formatted((ConsumerConfiguration)o);
        }
        if (o instanceof Headers) {
            Headers h = (Headers)o;
            boolean notFirst = false;
            StringBuilder sb = new StringBuilder("[");
            for (String key : h.keySet()) {
                if (notFirst) {
                    sb.append(',');
                }
                else {
                    notFirst = true;
                }
                sb.append(key).append("=").append(h.get(key));
            }
            return sb.append(']').toString();
        }
        if (o instanceof JsonSerializable) {
            return ((JsonSerializable)o).toJson();
        }
        if (o instanceof byte[]) {
            byte[] bytes = (byte[])o;
            if (bytes.length == 0) {
                return "<byte[0]>";
            }
            return new String((byte[])o);
        }
        if (o instanceof String[]) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String s : (String[])o) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(", ");
                }
                sb.append('\'')
                    .append(s == null ? "<null>" : (s.isEmpty() ? "<empty>" : s))
                    .append('\'');
            }
            return sb.toString();
        }
        String s = o.toString();
        return s.isEmpty() ? "<empty>" : s;
    }

    public static void debugHdr(int indent, Message msg) {
        Headers h = msg.getHeaders();
        if (h != null && !h.isEmpty()) {
            String pad = PAD.substring(0, indent);
            for (String key : h.keySet()) {
                DEBUG_PRINTER.println(pad + key + "=" + h.get(key));
            }
        }
    }

    public static void streamAndConsumer(Connection nc, String stream, String conName) throws IOException, JetStreamApiException {
        streamAndConsumer(nc.jetStreamManagement(), stream, conName);
    }

    public static void streamAndConsumer(JetStreamManagement jsm, String stream, String conName) throws IOException, JetStreamApiException {
        printStreamInfo(jsm.getStreamInfo(stream));
        printConsumerInfo(jsm.getConsumerInfo(stream, conName));
    }

    public static void consumer(Connection nc, String stream, String conName) throws IOException, JetStreamApiException {
        consumer(nc.jetStreamManagement(), stream, conName);
    }

    public static void consumer(JetStreamManagement jsm, String stream, String conName) throws IOException, JetStreamApiException {
        ConsumerInfo ci = jsm.getConsumerInfo(stream, conName);
        DEBUG_PRINTER.println("Consumer pending=" + ci.getNumPending() + " waiting=" + ci.getNumWaiting() + " ackPending=" + ci.getNumAckPending());
    }

    public static void printStreamInfo(StreamInfo si) {
        printObject(si, "StreamConfiguration", "StreamState", "ClusterInfo", "Mirror", "subjects", "sources");
    }

    public static void printStreamInfoList(List<StreamInfo> list) {
        printObject(list, "!StreamInfo", "StreamConfiguration", "StreamState");
    }

    public static void printConsumerInfo(ConsumerInfo ci) {
        printObject(ci, "ConsumerConfiguration", "Delivered", "AckFloor");
    }

    public static void printConsumerInfoList(List<ConsumerInfo> list) {
        printObject(list, "!ConsumerInfo", "ConsumerConfiguration", "Delivered", "AckFloor");
    }

    public static void printObject(Object o, String... subObjectNames) {
        String s = o.toString();
        for (String sub : subObjectNames) {
            boolean noIndent = sub.startsWith("!");
            String sb = noIndent ? sub.substring(1) : sub;
            String rx1 = ", " + sb;
            String repl1 = (noIndent ? ",\n": ",\n    ") + sb;
            s = s.replace(rx1, repl1);
        }
        DEBUG_PRINTER.println(s);
    }

    public static String pad2(int n) {
        return n < 10 ? " " + n : "" + n;
    }

    public static String pad3(int n) {
        return n < 10 ? "  " + n : (n < 100 ? " " + n : "" + n);
    }

    public static String pad3z(int n) {
        return n < 10 ? "00" + n : (n < 100 ? "0" + n : "" + n);
    }

    public static String yn(boolean b) {
        return b ? "Yes" : "No ";
    }

    public static String FN = "\n  ";
    public static String FBN = "{\n  ";
    public static String formatted(JsonSerializable j) {
        return j.getClass().getSimpleName() + j.toJson()
            .replace("{\"", FBN + "\"").replace(",", "," + FN);
    }

    public static String formatted(Object o) {
        return formatted(o.toString());
    }

    public static String formatted(String s) {
        return s.replace("{", FBN).replace(", ", "," + FN);
    }

    public static String consumerInfoString(ConsumerInfo ci) {
        return ci == null ? "null" :
            "Consumer{" +
                "pending=" + ci.getNumPending() +
                ", waiting=" + ci.getNumWaiting() +
                ", ackPending=" + ci.getNumAckPending() +
                ", redelivered=" + ci.getRedelivered() +
                ", delivered=" + sequenceInfoString(ci.getDelivered()) +
                ", ackFloor=" + sequenceInfoString(ci.getAckFloor()) +
                "} ";
    }

    public static String sequenceInfoString(SequenceInfo si) {
        return si == null ? "null" :
            "{" +
                "consumerSeq=" + si.getConsumerSequence() +
                ", streamSeq=" + si.getStreamSequence() +
                ", lastActive=" + zdtString(si.getLastActive()) +
                '}';
    }

    public static String metaDataString(NatsJetStreamMetaData meta) {
        return meta == null ? "null" :
            "Meta{" +
                "delivered=" + meta.deliveredCount() +
                ", streamSeq=" + meta.streamSequence() +
                ", consumerSeq=" + meta.consumerSequence() +
                ", pending=" + meta.pendingCount() +
                ", timestamp=" + zdtString(meta.timestamp()) +
                '}';
    }

    private static String serverInfoString(int indent, ServerInfo si) {
        String pad = PAD.substring(0, indent);
        return si.toString()
            .replace("ServerInfo{", "ServerInfo ")
            .replace(", ", "\n" + pad)
            .replace("}", "")
            ;
    }

    public static String zdtString(ZonedDateTime zdt) {
        return zdt == null ? "null" : zdt.toLocalTime().toString();
    }
}
