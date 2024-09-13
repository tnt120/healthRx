package com.healthrx.backend.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Getter
@AllArgsConstructor
@JsonSerialize(using = BusinessErrorCodes.ErrorSerializer.class)
public enum BusinessErrorCodes {

    NO_CODE(0, "No code", HttpStatus.NOT_IMPLEMENTED),
    INVALID_TOKEN(100, "Invalid token", HttpStatus.FORBIDDEN),
    ACCESS_TOKEN_EXPIRED(300, "Access token expired", HttpStatus.UNAUTHORIZED),
    VERIFICATION_TOKEN_EXPIRED(301, "Verification token expired", HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS(302, "Login and / or password is incorrect", HttpStatus.UNAUTHORIZED),
    ALREADY_EXISTS(303, "User with this email already exists", HttpStatus.CONFLICT),
    INVALID_USER(304, "Wrong refresh token", HttpStatus.FORBIDDEN),
    INVALID_VERIFICATION(305, "Invalid verification or wrong verification token", HttpStatus.FORBIDDEN),
    INVALID_EMAIL(306, "Account with provided email does not exists", HttpStatus.FORBIDDEN),
    NOT_VERIFIED_ACCOUNT(307, "Account is not verified", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(308, "User not found", HttpStatus.NOT_FOUND),
    PARAMETER_NOT_FOUND(309, "Provided parameter not found", HttpStatus.NOT_FOUND),
    PARAMETER_LOG_ALREADY_EXISTS(310, "Parameter log already exists. Post message incorrect", HttpStatus.BAD_REQUEST),
    PARAMETER_LOG_NOT_FOUND(311, "Parameter log not found", HttpStatus.NOT_FOUND),
    INVALID_USER_REQUEST(312, "User try to modify someone else data", HttpStatus.FORBIDDEN),
    DRUG_NOT_FOUND(313, "Drug not found", HttpStatus.NOT_FOUND),
    USER_DRUG_ALREADY_EXISTS(314, "User drug already exists", HttpStatus.CONFLICT),
    USER_NOT_PERMITTED(315, "User not permitted", HttpStatus.FORBIDDEN),
    USER_DRUG_NOT_FOUND(316, "User drug not found", HttpStatus.NOT_FOUND),
    DRUG_LOG_ALREADY_EXISTS(317, "Drug log already exists. Post message incorrect", HttpStatus.BAD_REQUEST),
    WRONG_DRUG_MONITOR_DATA(318, "Provided date and/or time are incorrect", HttpStatus.BAD_REQUEST),
    DRUG_LOG_NOT_FOUND(319, "Drug log not found", HttpStatus.NOT_FOUND),
    ACCOUNT_SETTINGS_NOT_FOUND(320, "Account settings not found", HttpStatus.NOT_FOUND),
    INCORRECT_CURRENT_PASSWORD(321, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    NEW_PASSWORDS_NOT_MATCH(322, "New passwords do not match", HttpStatus.BAD_REQUEST),
    NO_DIFFERENT_NEW_PASSWORD(323, "New password is the same as the old one", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    public ExceptionResponse getError() {
        return new ExceptionResponse(this);
    }

    public static class ErrorSerializer extends JsonSerializer<BusinessErrorCodes> {

        @Override
        public void serialize(BusinessErrorCodes value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("message", value.getDescription());
            gen.writeNumberField("code", value.getCode());
            gen.writeEndObject();
        }
    }
}
