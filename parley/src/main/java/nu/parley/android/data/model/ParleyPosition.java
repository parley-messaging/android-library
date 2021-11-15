package nu.parley.android.data.model;

public enum ParleyPosition {
    ;
    public enum Vertical {
        TOP(0, "top"),
        BOTTOM(2, "bottom");

        private final int intValue;
        private final String stringValue;

        private Vertical(int intValue, String stringValue) {
            this.intValue = intValue;
            this.stringValue = stringValue;
        }

        public static Vertical from(Integer integerValue) {
            for (Vertical position : values()) {
                if (position.intValue == integerValue) {
                    return position;
                }
            }
            // Default is TOP
            return TOP;
        }

        public static Vertical from(String stringValue) {
            for (Vertical position : values()) {
                if (position.stringValue.equals(stringValue)) {
                    return position;
                }
            }
            // Default is TOP
            return TOP;
        }
    }

    public enum Horizontal {
        LEFT,
        RIGHT
    }
}
