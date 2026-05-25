package roomescape.domain.reservationtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import roomescape.domain.reservationtime.dto.ReservationTimeAvailabilityResponse;
import roomescape.domain.reservationtime.dto.ReservationTimeResponse;
import roomescape.domain.reservationtime.dto.TimeCreationRequest;
import roomescape.domain.reservationtime.dto.TimeCreationResponse;
import roomescape.support.exception.ReservationTimeErrorCode;
import roomescape.support.exception.RoomescapeException;

@Transactional
@SpringBootTest
@Sql("/truncate.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;

    @DisplayName("성공 케이스")
    @Nested
    class SuccessCases {

        @Test
        @DisplayName("새로운 예약 시간을 생성한다.")
        void createReservationTime() {
            // given
            TimeCreationRequest request = new TimeCreationRequest(LocalTime.of(11, 30));

            // when
            TimeCreationResponse response = reservationTimeService.createReservationTime(request);

            // then
            assertThat(response.startAt()).isEqualTo(LocalTime.of(11, 30));
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("특정 날짜와 테마에 대한 예약 가능 시간 목록을 조회한다.")
        void getReservationTimeAvailability() {
            // given
            long themeId = 1L; // 정조 대왕의 비밀 (10:00, 12:00, 14:00, 16:00 예약됨)
            long dateId = 1L;  // 어제 날짜

            // when
            List<ReservationTimeAvailabilityResponse> responses = reservationTimeService.getReservationTimeAvailability(themeId, dateId);

            // then
            assertThat(responses).hasSize(7);
            assertThat(responses).extracting(ReservationTimeAvailabilityResponse::available)
                .containsExactly(false, false, false, false, true, true, true);
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("전체 예약 시간 목록을 조회한다.")
        void getAllReservationTime() {
            // when
            List<ReservationTimeResponse> responses = reservationTimeService.getAllReservationTime();

            // then
            assertThat(responses).hasSize(7);
            assertThat(responses).extracting(ReservationTimeResponse::startAt)
                .contains(LocalTime.of(10, 0), LocalTime.of(12, 0), LocalTime.of(22, 0));
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("ID로 예약 시간을 조회한다.")
        void findById() {
            // when
            ReservationTime reservationTime = reservationTimeService.findById(1L);

            // then
            assertThat(reservationTime.getStartAt()).isEqualTo(LocalTime.of(10, 0));
        }

        @Test
        @DisplayName("예약이 없는 시간을 삭제한다.")
        void deleteReservationTime() {
            // given
            TimeCreationResponse created = reservationTimeService.createReservationTime(new TimeCreationRequest(LocalTime.of(1, 0)));

            // when
            reservationTimeService.deleteReservationTime(created.id());

            // then
            assertThat(reservationTimeService.getAllReservationTime()).isEmpty();
        }
    }

    @DisplayName("실패 케이스")
    @Nested
    class FailCases {

        @Test
        @Sql("/reservation.sql")
        @DisplayName("중복된 시간을 생성하면 예외가 발생한다.")
        void createDuplicateTime() {
            // given
            TimeCreationRequest request = new TimeCreationRequest(LocalTime.of(10, 0));

            // when & then
            assertThatThrownBy(() -> reservationTimeService.createReservationTime(request))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.RESERVATION_TIME_DUPLICATED);
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("예약이 존재하는 시간을 삭제하면 예외가 발생한다.")
        void deleteTimeInUse() {
            // when & then
            assertThatThrownBy(() -> reservationTimeService.deleteReservationTime(1L))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.RESERVATION_TIME_IN_USE);
        }

        @Test
        @DisplayName("존재하지 않는 ID의 시간을 조회하면 예외가 발생한다.")
        void findByIdNotFound() {
            // when & then
            assertThatThrownBy(() -> reservationTimeService.findById(999L))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.RESERVATION_TIME_NOT_EXIST);
        }
    }
}
