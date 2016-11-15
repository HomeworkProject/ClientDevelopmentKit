package de.mlessmann.internals.data;

import de.mlessmann.api.data.IHWAttachment;
import de.mlessmann.api.data.IHWAttachmentLocation;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 03.11.16.
 */
public class HWAttachment implements IHWAttachment {

    private int position;
    private JSONObject attachmentJSON;

    public HWAttachment(JSONObject attachmentJSON, int position) {
        this.attachmentJSON = attachmentJSON;
    }

    public boolean isValid() {
        return !getID().equals("null")
                && attachmentJSON.optJSONObject("location")!=null;
    }

    @Override
    public String getTitle() {
        return attachmentJSON.optString("title");
    }

    @Override
    public String getDescription() {
        return attachmentJSON.optString("desc");
    }

    @Override
    public String getID() {
        return attachmentJSON.optString("id", "null");
    }

    @Override
    public IHWAttachmentLocation getLocation() {
        return new HWAttachmentLocation(attachmentJSON.optJSONObject("location"));
    }
}

