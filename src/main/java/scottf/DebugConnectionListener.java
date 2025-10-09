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

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;

public class DebugConnectionListener implements ConnectionListener {
    String label;

    public DebugConnectionListener() {
        this(null);
    }

    public DebugConnectionListener(String label) {
        label(label);
    }

    public void label(String clLabel) {
        this.label = clLabel == null ? "CL" : clLabel;
    }

    @Override
    public void connectionEvent(Connection conn, Events type) {
        if (label != null) {
            Debug.info(label, string(conn), "Event: %s", type.getEvent());
        }
    }

    @Override
    public void connectionEvent(Connection conn, Events type, Long time, String uriDetails) {
        if (label != null) {
            Debug.info(label, "@%s", time, string(conn), "Event: %s", type.getEvent(), uriDetails);
        }
    }

    private String string(Connection conn) {
        return "Connection(" + conn.hashCode() + ") " + conn.getStatus();
    }
}
