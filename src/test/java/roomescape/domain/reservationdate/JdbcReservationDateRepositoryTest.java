package roomescape.domain.reservationdate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
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
@Import(JdbcReservationDateRepository.class)
class JdbcReservationDateRepositoryTest {

    @Autowired
    private ReservationDateRepository reservationDateRepository;

    @Test
    @DisplayName("예약 날짜를 저장한다.")
    void save_reservation_date() {
        // given
        LocalDate playDay = LocalDate.now();
        ReservationDate reservationDate = ReservationDate.createWithoutId(playDay);

        // when
        ReservationDate saved = reservationDateRepository.save(reservationDate);

        // then
        assertThat(saved.getPlayDay()).isEqualTo(reservationDate.getPlayDay());
    }

    @Test
    @DisplayName("예약 날짜를 아이디로 조회한다.")
    void find_reservation_date_by_id() {
        // given
        LocalDate playDay = LocalDate.now();
        ReservationDate reservationDate = ReservationDate.createWithoutId(playDay);
        ReservationDate saved = reservationDateRepository.save(reservationDate);
        Long id = saved.getId();

        // when
        Optional<ReservationDate> findReservationDate = reservationDateRepository.findById(id);

        // then
        assertThat(findReservationDate).isPresent();
        ReservationDate actual = findReservationDate.get();
        assertThat(actual.getPlayDay()).isEqualTo(reservationDate.getPlayDay());
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 조회하면 빈 Optional을 반환한다.")
    void find_reservation_date_by_id_not_exist() {
        // given
        Long id = 9999L;

        // when
        Optional<ReservationDate> findReservationDate = reservationDateRepository.findById(id);

        // then
        assertThat(findReservationDate).isEmpty();
    }

    @Test
    @DisplayName("예약 날짜를 조회한다.")
    void find_all_reservation_date() {
        // given
        LocalDate playDay1 = LocalDate.now();
        ReservationDate reservationDate1 = ReservationDate.createWithoutId(playDay1);
        reservationDateRepository.save(reservationDate1);
        LocalDate playDay2 = LocalDate.now().plusDays(1);
        ReservationDate reservationDate2 = ReservationDate.createWithoutId(playDay2);
        reservationDateRepository.save(reservationDate2);
        LocalDate playDay3 = LocalDate.now().plusDays(2);
        ReservationDate reservationDate3 = ReservationDate.createWithoutId(playDay3);
        reservationDateRepository.save(reservationDate3);

        // when
        List<ReservationDate> reservationDates = reservationDateRepository.findAll();

        // then
        assertThat(reservationDates).hasSize(3);
    }

    @Test
    @DisplayName("예약 날짜를 아이디로 삭제한다.")
    void delete_reservation_date_by_id() {
        // given
        ReservationDate reservationDate = ReservationDate.createWithoutId(LocalDate.now());
        ReservationDate saved = reservationDateRepository.save(reservationDate);
        Long id = saved.getId();

        // when
        reservationDateRepository.deleteById(id);

        // then
        assertThat(reservationDateRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 삭제하면 0을 반환한다.")
    void delete_reservation_date_by_id_not_exist() {
        // given
        Long id = 9999L;

        // when
        int deleteCount = reservationDateRepository.deleteById(id);

        // then
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    @DisplayName("해당 날짜가 있으면 true를 반환한다.")
    void check_exist_play_day() {
        // given
        ReservationDate savedDate = reservationDateRepository.save(ReservationDate.createWithoutId(LocalDate.now()));

        // when
        boolean exist = reservationDateRepository.existsByPlayDay(savedDate.getPlayDay());

        // then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("해당 날짜가 없으면 false를 반환한다.")
    void check_not_exist_play_day() {
        // given
        ReservationDate notExistDate = ReservationDate.createWithoutId(LocalDate.of(2226, 5, 21));

        // when
        boolean exist = reservationDateRepository.existsByPlayDay(notExistDate.getPlayDay());

        // then
        assertThat(exist).isFalse();
    }
}
