package com.project.humanresource.dto.response;

import com.project.humanresource.utility.AssignmentCategory;

public record ResourceBasicInfoDto(
    Long id,
    String name,
    String resourceIdentifier, // e.g., Serial Number, Asset Tag
    AssignmentCategory category // Category of the resource itself
) {} 