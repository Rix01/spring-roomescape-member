package roomescape.domain.reservationtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@JdbcTest
@Sql("/truncate.sql")
@Import(JdbcReservationTimeRepository.class)
class JdbcReservationTimeRepositoryTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Test
    @DisplayName("예약 시간을 저장한다.")
    void save_reservation_time() {
        // given
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(11, 0));

        // when
        ReservationTime saved = reservationTimeRepository.save(reservationTime);

        // then
        assertThat(saved.getStartAt()).isEqualTo(reservationTime.getStartAt());
    }

    @Test
    @DisplayName("예약 시간을 아이디로 조회한다.")
    void find_reservation_time_by_id() {
        // given
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(11, 0));
        ReservationTime saved = reservationTimeRepository.save(reservationTime);

        // when
        Optional<ReservationTime> findReservationTime = reservationTimeRepository.findById(saved.getId());

        // then
        assertThat(findReservationTime).isPresent();
        ReservationTime actual = findReservationTime.get();
        assertThat(actual.getStartAt()).isEqualTo(reservationTime.getStartAt());
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 조회하면 빈 Optional을 반환한다.")
    void find_reservation_time_by_id_not_exist() {
        // given
        Long id = 9999L;

        // when
        Optional<ReservationTime> findReservationTime = reservationTimeRepository.findById(id);

        // then
        assertThat(findReservationTime).isEmpty();
    }

    @Test
    @DisplayName("예약 시간을 조회한다.")
    void find_all_reservation_time() {
        // given
        ReservationTime reservationTime1 = ReservationTime.createWithoutId(LocalTime.of(11, 0));
        reservationTimeRepository.save(reservationTime1);
        ReservationTime reservationTime2 = ReservationTime.createWithoutId(LocalTime.of(12, 0));
        reservationTimeRepository.save(reservationTime2);
        ReservationTime reservationTime3 = ReservationTime.createWithoutId(LocalTime.of(13, 0));
        reservationTimeRepository.save(reservationTime3);

        // when
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();

        // then
        assertThat(reservationTimes).hasSize(3);
    }

    @Test
    @DisplayName("예약 시간을 아이디로 삭제한다.")
    void delete_reservation_time_by_id() {
        // given
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(11, 0));
        ReservationTime saved = reservationTimeRepository.save(reservationTime);
        Long id = saved.getId();

        // when
        reservationTimeRepository.deleteById(id);

        // then
        assertThat(reservationTimeRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 삭제하면 0을 반환한다.")
    void delete_reservation_time_by_id_not_exist() {
        // given
        Long id = 9999L;

        // when
        int deleteCount = reservationTimeRepository.deleteById(id);

        // then
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    @DisplayName("해당 시간이 있으면 true를 반환한다.")
    void check_exist_start_at() {
        // given
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(11, 0));
        reservationTimeRepository.save(reservationTime);

        // when
        boolean exist = reservationTimeRepository.existsByStartAt(reservationTime.getStartAt());

        // then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("해당 시간이 없으면 false를 반환한다.")
    void check_not_exist_start_at() {
        // given
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(11, 0));

        // when
        boolean exist = reservationTimeRepository.existsByStartAt(reservationTime.getStartAt());

        // then
        assertThat(exist).isFalse();
    }
}
