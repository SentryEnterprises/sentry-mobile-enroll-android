package com.sentryenterprises.sentry.sdk.apdu

enum class APDUResponseCode(val value: Int) {
    /// Normal operation.
    OPERATION_SUCCESSFUL(0x9000),

    /// Warning processing - state of non-volatile memory may have changed
    NO_MATCH_FOUND(0x6300),
    PIN_INCORRECT_THREE_REMAIN(0x63C3),
    PIN_INCORRECT_TWO_REMAIN(0x63C2),
    PIN_INCORRECT_ONE_REMAIN(0x63C1),
    PIN_INCORRECT_ZERO_REMAIN(0x63C0),

    /// Checking errors - wrong length
    WRONG_LENGTH(0x6700),
    FORMAT_NOT_COMPLIANT(0x6701),
    LENGTH_VALUE_NOT_ONE_EXPECTED(0x6702),
    COMMUNICATION_FAILURE(0x6741),              // IDEX Enroll applet specific
    FINGER_REMOVED(0x6745),                     // IDEX Enroll applet specific
    POOR_IMAGE_QUALITY(0x6747),                  // IDEX Enroll applet specific
    USER_TIMEOUT_EXPIRED(0x6748),                // IDEX Enroll applet specific
    HOST_INTERFACE_TIMEOUT_EXPIRED(0x6749),       // IDEX Enroll applet specific

    /// Checking errors - command not allowed
    CONDITION_OF_USE_NOT_SATISFIED(0x6985),

    /// Checking errors - wrong parameters
    NOT_ENOUGH_MEMORY(0x6A84),

    /// Checking errors - wrong parameters
    WRONG_PARAMETERS(0x6B00),

    /// Checking errors - INS code not supported
    INSTRUCTION_BYTE_NOT_SUPPORTED(0x6D00),

    /// Checking errors - CLA code not supported
    CLASS_BYTE_NOT_SUPPORTED(0x6E00),

    /// Checking errors - no precise diagnosis
    COMMAND_ABORTED(0x6F00),
    NO_PRECISE_DIAGNOSIS(0x6F87),
    CARD_DEAD(0x6FFF)
}