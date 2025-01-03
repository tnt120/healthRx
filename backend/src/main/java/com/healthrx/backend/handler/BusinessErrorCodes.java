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
    EMAIL_ALREADY_EXISTS(324, "Email already exists", HttpStatus.CONFLICT),
    NOTIFICATIONS_DATA_BAD_REQUEST(325, "Bad notifications change request data", HttpStatus.BAD_REQUEST),
    DOCTOR_DETAILS_NOT_FOUND(326, "Doctor details not found", HttpStatus.NOT_FOUND),
    WRONG_INVITATION_TARGET(328, "Wrong invitation target, target must be doctor", HttpStatus.BAD_REQUEST),
    INVITATION_NOT_FOUND(329, "Invitation not found", HttpStatus.NOT_FOUND),
    INVITATION_ALREADY_ACCEPTED_REJECTED(330, "Invitation already accepted or rejected", HttpStatus.CONFLICT),
    INVITATION_ALREADY_ACCEPTED(331, "Invitation already accepted", HttpStatus.CONFLICT),
    INVITATION_ALREADY_EXISTS(331, "Invitation already exists", HttpStatus.CONFLICT),
    INVITATION_NOT_ACCEPTED(332, "Invitation not accepted", HttpStatus.CONFLICT),
    FRIENDSHIP_NOT_FOUND(333, "Friendship not found", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(334, "Message not found", HttpStatus.NOT_FOUND),
    ACTIVITY_NOT_FOUND(335, "Activity not found", HttpStatus.NOT_FOUND),
    ACTIVITY_LOG_NOT_FOUND(336, "Activity log not found", HttpStatus.NOT_FOUND),
    DOCTOR_ALREADY_VERIFIED(337, "Doctor already verified", HttpStatus.CONFLICT),
    DOCTOR_ALREADY_NOT_VERIFIED(338, "Doctor already not verified", HttpStatus.CONFLICT),
    NOT_PARAMS_STATS_PERMISSION(339, "User does not have permission to view parameters statistics", HttpStatus.FORBIDDEN),
    NOT_DRUGS_STATS_PERMISSION(340, "User does not have permission to view drugs statistics", HttpStatus.FORBIDDEN),
    NOT_ACTIVITIES_STATS_PERMISSION(341, "User does not have permission to view activities statistics", HttpStatus.FORBIDDEN),
    UNIT_NOT_FOUND(342, "Unit not found", HttpStatus.NOT_FOUND),
    PARAMETER_ALREADY_EXISTS(343, "Parameter already exists", HttpStatus.CONFLICT),
    ACTIVITY_ALREADY_EXISTS(344, "Activity already exists", HttpStatus.CONFLICT),
    USER_ALREADY_HAS_ROLE(345, "User already has this role", HttpStatus.CONFLICT),
    CANNOT_SET_ROLE(346, "Cannot set this role", HttpStatus.BAD_REQUEST),
    CANNOT_CHANGE_ROLE(347, "Cannot change this role", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_HEAD_ADMIN(348, "Cannot delete head admin", HttpStatus.BAD_REQUEST),
    WRONG_BMI_DATA(349, "Provided weight or height are incorrect", HttpStatus.BAD_REQUEST),
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
