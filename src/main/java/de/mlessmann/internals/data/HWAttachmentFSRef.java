package de.mlessmann.internals.data;

import de.mlessmann.api.data.IHWAttachment;
import de.mlessmann.api.data.IHWAttachmentFSRef;
import de.mlessmann.common.annotations.Nullable;

import java.io.File;

/**
 * Created by Life4YourGames on 15.11.16.
 */
public class HWAttachmentFSRef implements IHWAttachmentFSRef {

    private IHWAttachment attachment;
    private File file;
    private HWAttachmentFSRef.FileType fileType;

    public HWAttachmentFSRef(File file, IHWAttachment attachment) {
        this.file = file;
        this.attachment = attachment;
        String name = attachment.getLocation().getName().toLowerCase();
        if (name.endsWith(".pdf")) {
            this.fileType = FileType.PFD;
        } else if (name.endsWith(".txt")) {
            this.fileType = FileType.TEXT;
        } else if (name.endsWith(".jpg")
                || name.endsWith(".jpeg")
                || name.endsWith(".png")
                || name.endsWith(".bmp")
                || name.endsWith(".gif")) {
            this.fileType = FileType.IMG;
        } else if (name.endsWith(".svg")) {
            this.fileType = FileType.VECTORIMG;
        } else {
            this.fileType = FileType.UNKNOWN;
        }
    }

    @Nullable
    @Override
    public File getFile() {
        return file;
    }

    @Override
    public IHWAttachmentFSRef.FileType getFileType() {
        return fileType;
    }

    @Override
    public IHWAttachment getAttachment() {
        return attachment;
    }


}
