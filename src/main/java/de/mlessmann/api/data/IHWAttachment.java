package de.mlessmann.api.data;

import de.mlessmann.common.annotations.NotNull;
import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.internals.data.HWAttachment;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 03.11.16.
 */
public interface IHWAttachment {

    /**
     * The title of the attachment
     * This is additional information that can be provided by the user
     * @return The title of the attachment @code{null} means that the user didn't set any
     */
    @Nullable
    String getTitle();

    /**
     * The description of the attachment
     * This is additional information that can be provided by the user
     * @return The description of the attachment @code{null} means that the user didn't set any
     */
    @Nullable
    String getDescription();


    LocationType getLocType();

    /**
     * Filename of the attachment
     */
    String getName();

    /**
     * ID of the Homework this attachment belongs to
     */
    String getHWID();

    /**
     * Used to identify the attachment on the server
     * <code>null</code> if the attachment is not located on the server
     */
    @Nullable
    String getID();

    /**
     * Date of the Homework this attachment belongs to
     */
    int[] getDate();

    @Nullable
    String getURL();

    /**
     * A(!) JSONObject that represents this location
     * @return
     */
    JSONObject getJSON();

    /**
     * A guess on whether or not the attachment is available on the server
     * (The server marks attachments with getID!=null as virtual if the file is still missing)
     */
    boolean isVirtual();

    public enum LocationType {
        SERVER,
        WEB,
        INVALID
    }

    public class Builder {

        public static IHWAttachment.Builder builder() {
            return new Builder();
        }

        private LocationType type = LocationType.INVALID;
        private String hwID;
        private int[] date;
        private String name;
        private String URL;
        private String title;
        private String desc;

        /**
         * Attachment will be stored on the server
         * @param hwID ID of the Homework object
         * @param date Date of the Homework
         * @param name Name of the file
         * @return Configured builder
         */
        public Builder of(@NotNull String hwID, @NotNull int[] date, @NotNull String name) {
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
        public Builder of(@NotNull String hwID, @NotNull int[] date, @NotNull String name, @NotNull String URL) {
            this.hwID = hwID;
            this.date = date;
            this.name = name;
            this.URL = URL;
            this.type = LocationType.WEB;
            return this;
        }

        public Builder setTitle(@Nullable String title) {
            this.title = title;
            return this;
        }

        public Builder setDesc(@Nullable String desc) {
            this.desc = desc;
            return this;
        }

        public IHWAttachment build() {
            JSONObject j = new JSONObject();
            j.put("ownerhw", hwID);
            j.put("name", name);
            j.put("date", date);
            if (URL!=null) {
                j.put("url", URL);
            }
            if (title!=null) {
                j.put("title", title);
            }
            if (desc!=null) {
                j.put("desc", desc);
            }
            j.put("virtual", true);
            return new HWAttachment(j);
        }

    }
}
