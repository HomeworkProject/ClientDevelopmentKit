package de.mlessmann.api.data;

import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.internals.data.HWAttachmentLocation;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 15.11.16.
 */
public interface IHWAttachmentLocation {

    LocationType getLocType();

    String getName();

    @Nullable
    String getHWID();

    @Nullable
    String getID();

    @Nullable
    int[] getDate();

    @Nullable
    String getURL();

    JSONObject getJSON();

    boolean isVirtual();

    public enum LocationType {
        SERVER,
        WEB,
        INVALID
    }

    public class Builder {

        public static IHWAttachmentLocation.Builder builder() {
            return new Builder();
        }

        private LocationType type = LocationType.INVALID;
        private String hwID;
        private int[] date;
        private String name;
        private String URL;

        /**
         * Attachment will be stored on the server
         * @param hwID ID of the Homework object
         * @param date Date of the Homework
         * @param name Name of the file
         * @return Configured builder
         */
        public Builder of(String hwID, int[] date, String name) {
            this.hwID = hwID;
            this.date = date;
            this.name = name;
            this.URL = null;
            this.type = LocationType.SERVER;
            return this;
        }

        /**
         * Attachment will be stored on the web
         * @param hwID ID of the Homework object
         * @param date Date of the Homework
         * @param name Name of the file
         * @param URL URL of the file
         * @return Configured builder
         */
        public Builder of(String hwID, int[] date, String name, String URL) {
            this.hwID = hwID;
            this.date = date;
            this.name = name;
            this.URL = URL;
            this.type = LocationType.WEB;
            return this;
        }

        public IHWAttachmentLocation build() {
            JSONObject j = new JSONObject();
            j.put("ownerhw", hwID);
            j.put("name", name);
            j.put("date", date);
            if (URL!=null) {
                j.put("url", URL);
            }
            j.put("virtual", true);
            return new HWAttachmentLocation(j);
        }

    }
}
