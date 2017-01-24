/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: Jul 20, 2011                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.common;

import java.io.IOException;
import java.util.Scanner;

import javax.servlet.http.Part;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import org.oscm.internal.profile.POImageResource;
import org.oscm.internal.types.enumtypes.ImageType;
import org.oscm.internal.types.exception.ImageException;
import org.oscm.internal.vo.VOImageResource;

/**
 * Provides the UI image upload utility.
 * 
 * @author barzu
 */
public class ImageUploader {

    private ImageType imageType;
    private boolean deleteImage;
    private Part image;

    public ImageUploader(ImageType imageType) {
        this.imageType = imageType;
    }

    public VOImageResource getVOImageResource() throws ImageException {
        try {
            VOImageResource voImageResource = null;
            if (deleteImage) {
                voImageResource = new VOImageResource();
                voImageResource.setImageType(imageType);
                voImageResource.setBuffer(null);
                deleteImage = false;
            } else if (image != null) {
                voImageResource = new VOImageResource();
                voImageResource.setImageType(imageType);
                voImageResource.setContentType(image.getContentType());

                String fileContent = new Scanner(image.getInputStream())
                        .useDelimiter("\\A").next();
                voImageResource.setBuffer(fileContent.getBytes());
            }
            return voImageResource;
        } catch (IOException e) {
            String fileName = image != null ? "'" + image.getName() + "'" : "";
            throw new ImageException("The upload of the image file " + fileName
                    + " failed.", ImageException.Reason.UPLOAD, e);
        }
    }

    public POImageResource getPOImageResource() throws ImageException {
        POImageResource result = null;
        VOImageResource voImageResource = getVOImageResource();
        if (voImageResource != null) {
            result = new POImageResource(voImageResource);
        }
        return result;
    }

    public boolean isDeleteImage() {
        return deleteImage;
    }

    public void setDeleteImage(boolean deleteImage) {
        this.deleteImage = deleteImage;
    }

    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        this.image = image;
    }

}
