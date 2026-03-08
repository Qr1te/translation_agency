package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.request.CatToolRequest;
import com.qritiooo.translationagency.dto.response.CatToolResponse;
import com.qritiooo.translationagency.model.CatTool;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CatToolMapper {

    public static CatToolResponse toResponse(CatTool tool) {
        return new CatToolResponse(
                tool.getId(),
                tool.getName(),
                tool.getVendor(),
                tool.getCurrentVersion(),
                tool.getCloudBased()
        );
    }

    public static void updateEntity(CatTool tool, CatToolRequest request) {
        tool.setName(request.getName());
        tool.setVendor(request.getVendor());
        tool.setCurrentVersion(request.getCurrentVersion());
        tool.setCloudBased(request.getCloudBased());
    }

    public static void patchEntity(CatTool tool, CatToolRequest request) {
        if (request.getName() != null) {
            tool.setName(request.getName());
        }
        if (request.getVendor() != null) {
            tool.setVendor(request.getVendor());
        }
        if (request.getCurrentVersion() != null) {
            tool.setCurrentVersion(request.getCurrentVersion());
        }
        if (request.getCloudBased() != null) {
            tool.setCloudBased(request.getCloudBased());
        }
    }
}
