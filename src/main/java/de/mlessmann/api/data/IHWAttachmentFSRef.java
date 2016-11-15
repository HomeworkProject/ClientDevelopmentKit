package de.mlessmann.api.data;

import de.mlessmann.common.annotations.Nullable;

import java.io.File;

/**
 * Created by Life4YourGames on 13.11.16.
 */
public interface IHWAttachmentFSRef {

    @Nullable
    File getFile();

    FileType getFileType();

    IHWAttachment getAttachment();

    public enum FileType {
        PFD,
        IMG,
        VECTORIMG,
        TEXT,
        UNKNOWN
    }
}
