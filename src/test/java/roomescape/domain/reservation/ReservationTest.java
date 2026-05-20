package roomescape.domain.reservation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.domain.reservationdate.ReservationDate;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.support.exception.RoomescapeException;

class ReservationTest {

    @DisplayName("성공 케이스")
    @Nested
    class Success {

        @Test
        @DisplayName("예약을 생성할 수 있다.")
        void create_reservation() {
            // given
            Long id = 1L;
            String name = "이산";
            ReservationDate reservationDate = ReservationDate.of(1L, LocalDate.now());
            ReservationTime reservationTime = ReservationTime.of(1L, LocalTime.now());
            Theme theme = Theme.of(1L, "테마", "테마 내용", "/themes/theme");

            // when & then
            assertThatCode(() -> {
                Reservation.of(id, name, reservationDate, reservationTime, theme);
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("아이디 없이 예약을 생성할 수 있다.")
        void create_reservation_without_id() {
            // given
            String name = "이산";
            ReservationDate reservationDate = ReservationDate.of(1L, LocalDate.now());
            ReservationTime reservationTime = ReservationTime.of(1L, LocalTime.now());
            Theme theme = Theme.of(1L, "테마", "테마 내용", "/themes/theme");

            // when & then
            assertThatCode(() -> {
                Reservation.createWithoutId(name, reservationDate, reservationTime, theme);
            }).doesNotThrowAnyException();
        }
    }

    @DisplayName("실패 케이스")
    @Nested
    class Fail {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "    "})
        @DisplayName("예약자명은 null이거나 공백이면 예외가 발생한다.")
        void exception_when_name_null_or_blank(String invalidName) {
            // given
            Long id = 1L;
            String name = invalidName;
            ReservationDate reservationDate = ReservationDate.of(1L, LocalDate.now());
            ReservationTime reservationTime = ReservationTime.of(1L, LocalTime.now());
            Theme theme = Theme.of(1L, "테마", "테마 내용", "/themes/theme");

            // when & then
            assertThatThrownBy(() -> {
                Reservation.of(id, name, reservationDate, reservationTime, theme);
            }).isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("예약자 성명 데이터가 유효하지 않습니다.");
        }

        @Test
        @DisplayName("날짜가 null이면 예외가 발생한다")
        void exception_when_reservation_date_null() {
            // given
            Long id = 1L;
            String name = "이산";
            ReservationDate reservationDate = null;
            ReservationTime reservationTime = ReservationTime.of(1L, LocalTime.now());
            Theme theme = Theme.of(1L, "테마", "테마 내용", "/themes/theme");

            // when & then
            assertThatThrownBy(() -> {
                Reservation.of(id, name, reservationDate, reservationTime, theme);
            }).isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("예약 날짜 식별자 혹은 데이터가 누락되었습니다.");
        }

        @Test
        @DisplayName("시간이 null이면 예외가 발생한다")
        void exception_when_reservation_time_null() {
            // given
            Long id = 1L;
            String name = "이산";
            ReservationDate reservationDate = ReservationDate.of(1L, LocalDate.now());
            ReservationTime reservationTime = null;
            Theme theme = Theme.of(1L, "테마", "테마 내용", "/themes/theme");

            // when & then
            assertThatThrownBy(() -> {
                Reservation.of(id, name, reservationDate, reservationTime, theme);
            }).isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("예약 시간 식별자 혹은 데이터가 누락되었습니다.");
        }

        @Test
        @DisplayName("테마가 null이면 예외가 발생한다")
        void exception_when_theme_null() {
            // given
            Long id = 1L;
            String name = "이산";
            ReservationDate reservationDate = ReservationDate.of(1L, LocalDate.now());
            ReservationTime reservationTime = ReservationTime.of(1L, LocalTime.now());
            Theme theme = null;
            
            // when & then
            assertThatThrownBy(() -> {
                Reservation.of(id, name, reservationDate, reservationTime, theme);
            }).isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("테마 엔티티 식별자 정보가 누락되었습니다.");
        }
    }
}
