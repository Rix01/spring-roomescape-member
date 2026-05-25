package roomescape.domain.reservationdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservationdate.dto.AdminReservationDateResponse;
import roomescape.domain.reservationdate.dto.ReservationDateCreationRequest;
import roomescape.domain.reservationdate.dto.ReservationDateCreationResponse;
import roomescape.domain.reservationdate.dto.ReservationDateResponse;
import roomescape.support.exception.ReservationDateErrorCode;
import roomescape.support.exception.RoomescapeException;

@Transactional
@SpringBootTest
@Sql("/truncate.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class ReservationDateServiceTest {

    @Autowired
    private ReservationDateService reservationDateService;

    @DisplayName("성공 케이스")
    @Nested
    class SuccessCases {

        @Test
        @DisplayName("새로운 예약 날짜를 생성한다.")
        void createReservationDate() {
            // given
            LocalDate newDate = LocalDate.now().plusDays(10);
            ReservationDateCreationRequest request = new ReservationDateCreationRequest(newDate);

            // when
            ReservationDateCreationResponse response = reservationDateService.createReservationDate(request);

            // then
            assertThat(response.playDay()).isEqualTo(newDate);
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("사용자용 예약 가능 날짜 목록을 조회한다.")
        void getAllAvailableReservationDate() {
            // when
            List<ReservationDateResponse> responses = reservationDateService.getAllAvailableReservationDate();

            // then
            assertThat(responses).isEmpty();
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("관리자용 전체 예약 날짜 목록을 조회한다.")
        void getAllReservationDateForAdmin() {
            // when
            List<AdminReservationDateResponse> responses = reservationDateService.getAllReservationDateForAdmin();

            // then
            assertThat(responses).hasSize(7);
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("ID로 예약 날짜를 조회한다.")
        void findById() {
            // when
            ReservationDate reservationDate = reservationDateService.findById(1L);

            // then
            assertThat(reservationDate.getPlayDay()).isEqualTo(LocalDate.now().minusDays(1));
        }

        @Test
        @DisplayName("예약이 없는 날짜를 삭제한다.")
        void deleteReservationDate() {
            // given
            ReservationDateCreationResponse created = reservationDateService.createReservationDate(
                new ReservationDateCreationRequest(LocalDate.now().plusDays(10)));

            // when
            reservationDateService.deleteReservationDate(created.id());

            // then
            assertThat(reservationDateService.getAllReservationDateForAdmin()).isEmpty();
        }
    }

    @DisplayName("실패 케이스")
    @Nested
    class FailCases {

        @Test
        @Sql("/reservation.sql")
        @DisplayName("중복된 날짜를 생성하면 예외가 발생한다.")
        void createDuplicateDate() {
            // given
            LocalDate duplicatedDate = LocalDate.now().minusDays(1);
            ReservationDateCreationRequest request = new ReservationDateCreationRequest(duplicatedDate);

            // when & then
            assertThatThrownBy(() -> reservationDateService.createReservationDate(request))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationDateErrorCode.RESERVATION_DATE_DUPLICATED);
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("예약이 존재하는 날짜를 삭제하면 예외가 발생한다.")
        void deleteDateInUse() {
            // when & then
            assertThatThrownBy(() -> reservationDateService.deleteReservationDate(1L))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationDateErrorCode.RESERVATION_DATE_IN_USE);
        }

        @Test
        @DisplayName("존재하지 않는 ID의 날짜를 조회하면 예외가 발생한다.")
        void findByIdNotFound() {
            // when & then
            assertThatThrownBy(() -> reservationDateService.findById(999L))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationDateErrorCode.RESERVATION_DATE_NOT_EXIST);
        }
    }
}
