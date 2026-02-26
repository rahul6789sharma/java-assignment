package com.fulfilment.application.monolith.rest;

/**
 * Standard JSON body for API errors (business, validation, security). Used by exception mappers.
 */
public class ErrorResponse {

  private final String errorCode;
  private final String message;

  public ErrorResponse(String errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }
}
