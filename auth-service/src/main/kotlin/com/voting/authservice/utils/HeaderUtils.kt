package com.voting.authservice.utils

import com.voting.authservice.exception.MissingHeaderException
import jakarta.servlet.ServletRequest


fun ServletRequest.getClientId(): String = getParameter(ATTRIBUTE.CLIENT_ID_ATTRIBUTE) ?: throw MissingHeaderException(
    "${ATTRIBUTE.CLIENT_ID_ATTRIBUTE} header missing"
)