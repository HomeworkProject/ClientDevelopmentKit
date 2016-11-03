package de.mlessmann.internals.data;

import de.mlessmann.api.data.IHWAttachment;
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
                && getURL()!=null;
    }

    @Override
    public int getPosition() {
        return position;
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
    public String getURL() {
        return attachmentJSON.optString("url");
    }
}

