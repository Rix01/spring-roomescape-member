package roomescape.domain.reservationtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import roomescape.support.exception.RoomescapeException;

class ReservationTimeTest {

    @DisplayName("성공 케이스")
    @Nested
    class Success {

        @Test
        @DisplayName("예약 시간을 생성할 수 있다.")
        void create_reservation_time() {
            // given
            Long id = 1L;
            LocalTime startAt = LocalTime.of(12, 00);

            // when & then
            assertThatCode(() -> {
                ReservationTime.of(id, startAt);
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("아이디 없이 예약시간을 생성할 수 있다.")
        void create_reservation_time_without_id() {
            // given
            LocalTime startAt = LocalTime.of(12, 00);

            // when & then
            assertThatCode(() -> {
                ReservationTime.createWithoutId(startAt);
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("시간 형식은 초 단위를 제외한 HH:mm 문자열로 변환된다.")
        void format_time_HH_mm() {
            // given
            Long id = 1L;
            LocalTime startAt = LocalTime.of(11, 55, 45);
            ReservationTime reservationTime = ReservationTime.of(id, startAt);

            // when
            String result = reservationTime.getFormattedStartAt();

            // then
            assertThat(result).isEqualTo("11:55");
        }

        @Test
        @DisplayName("시간 형식은 초 단위를 제외한 HH:mm 문자열로 변환된다."
            + "한 자리수 시간은 숫자 앞에 0이 붙는다.")
        void format_time_HH_mm2() {
            // given
            Long id = 1L;
            LocalTime startAt = LocalTime.of(9, 5, 45);
            ReservationTime reservationTime = ReservationTime.of(id, startAt);

            // when
            String result = reservationTime.getFormattedStartAt();

            // then
            assertThat(result).isEqualTo("09:05");
        }
    }

    @DisplayName("실패 케이스")
    @Nested
    class Fail {

        @Test
        @DisplayName("예약 시간이 null이면 예외가 발생한다.")
        void exception_when_reservation_time_null() {
            // given
            Long id = 1L;
            LocalTime startAt = null;

            // when & then
            assertThatThrownBy(() -> {
                ReservationTime.of(id, startAt);
            }).isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("예약 시간 식별자 혹은 데이터가 누락되었습니다.");
        }
    }
}
