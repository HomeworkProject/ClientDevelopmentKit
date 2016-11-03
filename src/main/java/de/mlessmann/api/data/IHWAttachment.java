package de.mlessmann.api.data;

import de.mlessmann.api.annotations.Nullable;

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
     * Remember that hwserver:// will mean the document is stored on the server
     * and cannot be requested as normal web resources
     * @return The URL of the document
     */
    String getURL();

    /**
     * Attachment-ID
     * Used to identify this attachment within the HW-Object
     */
    String getID();

    /**
     * Position
     * desired y-Position of the attachment.
     * Used to keep track of the order of the attachments
     * since JSONLibs tend to move objects around.
     * @return the position of the attachment
     */
    int getPosition();
}
