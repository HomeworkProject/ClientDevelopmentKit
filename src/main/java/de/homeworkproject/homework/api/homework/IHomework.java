package de.homeworkproject.homework.api.homework;

import de.mlessmann.common.annotations.API;
import de.mlessmann.common.annotations.Nullable;

import java.util.List;

/**
 * Created by Life4YourGames on 09.08.16.
 */
@API
public interface IHomework extends IHWCarrier {

    boolean isDummy();

    String getId();

    String getSubject();

    int[] getDate();

    @Nullable
    String getTitle();

    String optTitle(String def);

    @Nullable
    String getDescription();

    String optDescription(String def);

    int getAttachmentCount();

    /**
     * Retrieve the full attachment information from the HWObj
     * @return List of attachment references
     */
    List<IHomeworkAttachment> getAttachments();
}