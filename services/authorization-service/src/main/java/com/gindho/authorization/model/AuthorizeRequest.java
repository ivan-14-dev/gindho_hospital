package com.gindho.authorization.model;

public record AuthorizeRequest(
    String userId,
    String permission,
    String resource,
    String action
) {}