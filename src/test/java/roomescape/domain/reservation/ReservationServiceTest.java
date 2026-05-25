package roomescape.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.dto.ReservationCreationRequest;
import roomescape.domain.reservation.dto.ReservationCreationResponse;
import roomescape.domain.reservation.dto.ReservationResponse;
import roomescape.domain.reservation.dto.ReservationUpdateRequest;
import roomescape.domain.reservationdate.ReservationDateService;
import roomescape.domain.reservationdate.dto.ReservationDateCreationRequest;
import roomescape.domain.reservationtime.ReservationTimeService;
import roomescape.domain.reservationtime.dto.TimeCreationRequest;
import roomescape.domain.theme.ThemeService;
import roomescape.domain.theme.dto.ThemeCreationRequest;
import roomescape.support.exception.ReservationDateErrorCode;
import roomescape.support.exception.ReservationErrorCode;
import roomescape.support.exception.ReservationTimeErrorCode;
import roomescape.support.exception.RoomescapeException;

@Transactional
@SpringBootTest
@Sql("/truncate.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationDateService reservationDateService;

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Autowired
    private ThemeService themeService;

    private Long createFutureDate() {
        return reservationDateService.createReservationDate(
            new ReservationDateCreationRequest(LocalDate.now().plusDays(1))).id();
    }

    private Long createTime() {
        return reservationTimeService.createReservationTime(new TimeCreationRequest(LocalTime.of(22, 0))).id();
    }

    private Long createTheme() {
        return themeService.createTheme(new ThemeCreationRequest("테마", "설명", "url")).id();
    }

    @DisplayName("성공 케이스")
    @Nested
    class SuccessCases {

        @Test
        @DisplayName("예약을 생성한다.")
        void createReservation() {
            // given
            Long dateId = createFutureDate();
            Long timeId = createTime();
            Long themeId = createTheme();
            ReservationCreationRequest request = new ReservationCreationRequest("브라운", dateId, timeId, themeId);

            // when
            ReservationCreationResponse response = reservationService.createReservation(request);

            // then
            assertThat(response.name()).isEqualTo("브라운");
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("전체 예약 목록을 조회한다.")
        void getAllReservations() {
            // when
            List<ReservationResponse> responses = reservationService.getAllReservations();

            // then
            assertThat(responses).hasSize(78);
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("이름으로 예약을 조회한다.")
        void getReservationsByName() {
            // when
            List<ReservationResponse> responses = reservationService.getReservationsByName("이순신");

            // then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).name()).isEqualTo("이순신");
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("예약을 삭제한다.")
        void deleteReservation() {
            // when
            reservationService.deleteReservation(1L);

            // then
            assertThat(reservationService.getAllReservations()).hasSize(77);
        }

        @Test
        @DisplayName("미래 예약을 취소한다.")
        void cancelReservation() {
            // given
            Long dateId = createFutureDate();
            Long timeId = createTime();
            Long themeId = createTheme();
            ReservationCreationResponse created = reservationService.createReservation(
                new ReservationCreationRequest("브라운", dateId, timeId, themeId));

            // when
            reservationService.cancelReservation(created.id());

            // then
            assertThat(reservationService.getAllReservations()).isEmpty();
        }

        @Test
        @DisplayName("미래 예약을 수정한다.")
        void updateReservation() {
            // given
            Long dateId = createFutureDate();
            Long timeId = createTime();
            Long themeId = createTheme();
            ReservationCreationResponse created = reservationService.createReservation(
                new ReservationCreationRequest("브라운", dateId, timeId, themeId));

            Long newTimeId = reservationTimeService.createReservationTime(new TimeCreationRequest(LocalTime.of(23, 0)))
                .id();
            ReservationUpdateRequest updateRequest = new ReservationUpdateRequest(dateId, newTimeId);

            // when
            ReservationResponse response = reservationService.updateReservation(created.id(), updateRequest);

            // then
            assertThat(response.time().startAt()).isEqualTo(LocalTime.of(23, 0));
        }
    }

    @DisplayName("실패 케이스")
    @Nested
    class FailCases {

        @Test
        @DisplayName("과거 날짜로 예약하면 예외가 발생한다.")
        void createReservationInPast() {
            // given
            Long dateId = reservationDateService.createReservationDate(
                new ReservationDateCreationRequest(LocalDate.now().minusDays(1))).id();
            Long timeId = createTime();
            Long themeId = createTheme();
            ReservationCreationRequest request = new ReservationCreationRequest("브라운", dateId, timeId, themeId);

            // when & then
            assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.PAST_TIME_NOT_ALLOWED);
        }

        @Test
        @DisplayName("중복된 예약을 생성하면 예외가 발생한다.")
        void createDuplicateReservation() {
            // given
            Long dateId = createFutureDate();
            Long timeId = createTime();
            Long themeId = createTheme();
            reservationService.createReservation(new ReservationCreationRequest("브라운", dateId, timeId, themeId));

            ReservationCreationRequest request = new ReservationCreationRequest("코니", dateId, timeId, themeId);

            // when & then
            assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.RESERVATION_DUPLICATED);
        }

        @Test
        @DisplayName("오늘 날짜의 예약을 취소하면 예외가 발생한다.")
        void cancelTodayReservation() {
            // given
            Long todayId = reservationDateService.createReservationDate(
                new ReservationDateCreationRequest(LocalDate.now())).id();
            Long futureTimeId = reservationTimeService.createReservationTime(
                new TimeCreationRequest(LocalTime.now().plusHours(1))).id();
            Long themeId = createTheme();
            ReservationCreationResponse created = reservationService.createReservation(
                new ReservationCreationRequest("브라운", todayId, futureTimeId, themeId));

            // when & then
            assertThatThrownBy(() -> reservationService.cancelReservation(created.id()))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationDateErrorCode.TODAY_NOT_MODIFIED);
        }

        @Test
        @DisplayName("존재하지 않는 ID의 예약을 취소하면 예외가 발생한다.")
        void cancelNonExistentReservation() {
            // when & then
            assertThatThrownBy(() -> reservationService.cancelReservation(999L))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("과거 예약을 취소하면 예외가 발생한다.")
        void cancelPastReservation() {
            // when & then
            assertThatThrownBy(() -> reservationService.cancelReservation(1L))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.PAST_TIME_NOT_ALLOWED);
        }

        @Test
        @DisplayName("오늘 날짜의 예약을 수정하면 예외가 발생한다.")
        void updateTodayReservation() {
            // given
            Long todayId = reservationDateService.createReservationDate(
                new ReservationDateCreationRequest(LocalDate.now())).id();
            Long futureTimeId = reservationTimeService.createReservationTime(
                new TimeCreationRequest(LocalTime.now().plusHours(1))).id();
            Long themeId = createTheme();
            ReservationCreationResponse created = reservationService.createReservation(
                new ReservationCreationRequest("브라운", todayId, futureTimeId, themeId));

            ReservationUpdateRequest request = new ReservationUpdateRequest(todayId, futureTimeId);

            // when & then
            assertThatThrownBy(() -> reservationService.updateReservation(created.id(), request))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationDateErrorCode.TODAY_NOT_MODIFIED);
        }
    }
}
