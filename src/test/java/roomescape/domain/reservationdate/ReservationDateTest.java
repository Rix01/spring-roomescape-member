package roomescape.domain.reservationdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import roomescape.support.exception.RoomescapeException;

class ReservationDateTest {

    @DisplayName("성공 케이스")
    @Nested
    class Success {

        @Test
        @DisplayName("예약일을 생성할 수 있다.")
        void create_reservation_date() {
            // given
            Long id = 1L;
            LocalDate playDay = LocalDate.of(2026, 5, 19);

            // when & then
            assertThatCode(() -> {
                ReservationDate.of(id, playDay);
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("아이디 없이 예약일을 생성할 수 있다.")
        void create_reservation_date_without_id() {
            // given
            LocalDate playDay = LocalDate.of(2026, 5, 19);

            // when & then
            assertThatCode(() -> {
                ReservationDate.createWithoutId(playDay);
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("오늘 날짜를 등록할 수 있다")
        void create_reservation_date_today() {
            // given
            Long id = 1L;
            LocalDate today = LocalDate.now();
            LocalDate playDay = LocalDate.now();
            ReservationDate reservationDate = ReservationDate.of(id, playDay);

            // when
            boolean isAvailable = reservationDate.isAvailable(today);

            // then
            assertThat(isAvailable).isTrue();
        }

        @Test
        @DisplayName("과거 날짜는 등록할 수 없다")
        void cannot_create_play_day_past() {
            // given
            Long id = 1L;
            LocalDate today = LocalDate.now();
            LocalDate playDay = LocalDate.now().minusDays(1);
            ReservationDate reservationDate = ReservationDate.of(id, playDay);

            // when
            boolean isAvailable = reservationDate.isAvailable(today);

            // then
            assertThat(isAvailable).isFalse();
        }
    }

    @DisplayName("실패 케이스")
    @Nested
    class Fail {

        @Test
        @DisplayName("예약일이 null이면 예외가 발생한다.")
        void exception_when_play_day_null() {
            // given
            Long id = 1L;
            LocalDate playDay = null;

            // when & then
            assertThatThrownBy(() -> {
                ReservationDate.of(id, playDay);
            }).isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("예약 날짜 데이터가 유효하지 않거나 누락되었습니다.");
        }
    }
}
