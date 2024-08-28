package nu.parley.android.util;

import nu.parley.android.Parley;
import nu.parley.android.data.model.Device;

public final class DeviceUtil {

    public static Device getDevice() {
        Device device = new Device();

        Parley parley = Parley.getInstance();
        device.setPushToken(parley.getPushToken(), parley.getPushType());
        device.setUserAdditionalInformation(parley.getUserAdditionalInformation());
        device.setReferrer(parley.getReferrer());

        return device;
    }
}
