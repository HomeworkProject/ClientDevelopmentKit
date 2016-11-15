package de.mlessmann.api.data;

import de.mlessmann.common.annotations.Nullable;

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

    /**
     * URL to the attachment.
     * Remember that ``hwserver://`` will mean the document is stored on the server
     * and cannot be requested as normal web resources
     * @return The URL of the document
     */
    IHWAttachmentLocation getLocation();

    /**
     * Attachment-ID
     * Used to identify this attachment within the HW-Object
     */
    String getID();
}
