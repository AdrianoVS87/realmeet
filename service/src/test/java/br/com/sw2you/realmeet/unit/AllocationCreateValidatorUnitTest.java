package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.util.DateUtils.DEFAULT_TIMEZONE;
import static br.com.sw2you.realmeet.util.DateUtils.now;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_NAME;
import static br.com.sw2you.realmeet.utils.TestDataCreator.*;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.util.DateUtils;
import br.com.sw2you.realmeet.validator.AllocationValidator;
import br.com.sw2you.realmeet.validator.RoomValidator;
import br.com.sw2you.realmeet.validator.ValidationError;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

class AllocationCreateValidatorUnitTest extends BaseUnitTest {
    private AllocationValidator victim;

    @Mock
    private AllocationRepository allocationRepository;

    @BeforeEach
    void setupEach() {
        victim = new AllocationValidator(allocationRepository);
    }

    @Test
    void testValidateWhenAllocationIsValid() {
        victim.validate(newCreateAllocationDTO());
    }

    @Test
    void testValidateWhenSubjectIsMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().subject(null))
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_SUBJECT, ALLOCATION_SUBJECT + MISSING),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenSubjectExceedsLength() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(
                    newCreateAllocationDTO().subject(StringUtils.rightPad("X", ALLOCATION_SUBJECT_MAX_LENGTH + 1, 'X'))
                )
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_SUBJECT, ALLOCATION_SUBJECT + EXCEEDS_MAX_LENGTH),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeNameIsMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().employeeName(null))
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_EMPLOYEE_NAME, ALLOCATION_EMPLOYEE_NAME + MISSING),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeNameExceedsLength() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(
                    newCreateAllocationDTO()
                        .employeeName(StringUtils.rightPad("X", ALLOCATION_EMPLOYEE_NAME_MAX_LENGTH + 1, 'X'))
                )
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_EMPLOYEE_NAME, ALLOCATION_EMPLOYEE_NAME + EXCEEDS_MAX_LENGTH),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeEmailIsMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().employeeEmail(null))
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_EMPLOYEE_EMAIL, ALLOCATION_EMPLOYEE_EMAIL + MISSING),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeEmailExceedsLength() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(
                    newCreateAllocationDTO()
                        .employeeEmail(StringUtils.rightPad("X", ALLOCATION_EMPLOYEE_EMAIL_MAX_LENGTH + 1, 'X'))
                )
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_EMPLOYEE_EMAIL, ALLOCATION_EMPLOYEE_EMAIL + EXCEEDS_MAX_LENGTH),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenStartAtIsMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().startAt(null))
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_START_AT, ALLOCATION_START_AT + MISSING),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenEndAtIsMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().endAt(null))
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_END_AT, ALLOCATION_END_AT + MISSING),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenDateOrderIsInvalid() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(
                    newCreateAllocationDTO().startAt(now().plusDays(1)).endAt(now().plusDays(1).minusMinutes(30))
                )
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_START_AT, ALLOCATION_START_AT + INCONSISTENT),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenDateIsInThePast() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().startAt(now().minusMinutes(30)).endAt(now().plusMinutes(30)))
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_START_AT, ALLOCATION_START_AT + IN_THE_PAST),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateWhenDateIntervalExceedsMaxDuration() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(
                    newCreateAllocationDTO()
                        .startAt(now().plusDays(1))
                        .endAt(now().plusDays(1).plusSeconds(ALLOCATION_MAX_DURATION_SECONDS + 1))
                )
        );
        assertEquals(1, exception.getValidationErros().getNumberOfErrors());
        assertEquals(
            new ValidationError(ALLOCATION_END_AT, ALLOCATION_END_AT + EXCEEDS_DURATION),
            exception.getValidationErros().getError(0)
        );
    }

    @Test
    void testValidateDateIntervals() {
        assertTrue(isScheduleAllowed(tommorowAt(4), tommorowAt(5), tommorowAt(1), tommorowAt(2)));
        assertTrue(isScheduleAllowed(tommorowAt(4), tommorowAt(5), tommorowAt(6), tommorowAt(7)));
        assertTrue(isScheduleAllowed(tommorowAt(4), tommorowAt(5), tommorowAt(3), tommorowAt(4)));
        assertTrue(isScheduleAllowed(tommorowAt(4), tommorowAt(5), tommorowAt(5), tommorowAt(6)));
        assertFalse(isScheduleAllowed(tommorowAt(4), tommorowAt(7), tommorowAt(4), tommorowAt(7)));
        assertFalse(isScheduleAllowed(tommorowAt(4), tommorowAt(7), tommorowAt(4), tommorowAt(5)));
        assertFalse(isScheduleAllowed(tommorowAt(4), tommorowAt(7), tommorowAt(6), tommorowAt(7)));
        assertFalse(isScheduleAllowed(tommorowAt(4), tommorowAt(7), tommorowAt(3), tommorowAt(5)));
        assertFalse(isScheduleAllowed(tommorowAt(4), tommorowAt(7), tommorowAt(6), tommorowAt(8)));
    }

    private OffsetDateTime tommorowAt(int hour) {
        return OffsetDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(hour, 0), DEFAULT_TIMEZONE);
    }

    private boolean isScheduleAllowed(
        OffsetDateTime scheduledAllocationStart,
        OffsetDateTime scheduledAllocationEnd,
        OffsetDateTime newAllocationStart,
        OffsetDateTime newAllocationEnd
    ) {
        given(allocationRepository.findAllWithFilters(any(), any(), any(), any()))
            .willReturn(
                List.of(
                    newAllocationBuilder(newRoomBuilder().build())
                        .startAt(scheduledAllocationStart)
                        .endAt(scheduledAllocationEnd)
                        .build()
                )
            );

        try {
            victim.validate(newCreateAllocationDTO().startAt(newAllocationStart).endAt(newAllocationEnd));
            return true;
        } catch (InvalidRequestException e) {
            return false;
        }
    }
}
