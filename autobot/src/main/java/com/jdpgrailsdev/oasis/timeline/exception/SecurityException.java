package com.jdpgrailsdev.oasis.timeline.exception;

/** Custom exception that denotes an error while attempting to encrypt/decrypt a value. */
public class SecurityException extends Exception {

  @SuppressWarnings("PMD.FieldNamingConventions")
  private static final long serialVersionUID = 1L;

  public SecurityException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
