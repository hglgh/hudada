package com.hgl.hudada.model.dto.file;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author 请别把我整破防
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;

    @Serial
    private static final long serialVersionUID = 1L;
}