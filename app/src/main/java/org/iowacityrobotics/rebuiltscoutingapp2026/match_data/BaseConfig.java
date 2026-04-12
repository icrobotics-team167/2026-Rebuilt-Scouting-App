// Claude, James A
// 03/20/2026 - 4/12/2026
// Base interface for scouting match fields.
package org.iowacityrobotics.rebuiltscoutingapp2026.match_data;

public interface BaseConfig {
    enum DataType { NUMBER, TEXT, BOOLEAN }

    class Field {
        public int viewId;
        public String jsonKey;
        public DataType type;

        public Field(int viewId, String jsonKey, DataType type) {
            this.viewId = viewId;
            this.jsonKey = jsonKey;
            this.type = type;
        }
    }
}