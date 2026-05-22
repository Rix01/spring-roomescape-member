package roomescape.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import roomescape.domain.reservationdate.JdbcReservationDateRepository;
import roomescape.domain.reservationdate.ReservationDate;
import roomescape.domain.reservationdate.ReservationDateRepository;
import roomescape.domain.reservationtime.JdbcReservationTimeRepository;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.JdbcThemeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;

@JdbcTest
@Sql("/truncate.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class JdbcReservationRepositoryTest {

    private ReservationRepository reservationRepository;
    private ReservationDateRepository reservationDateRepository;
    private ReservationTimeRepository reservationTimeRepository;
    private ThemeRepository themeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        reservationRepository = new JdbcReservationRepository(jdbcTemplate);
        reservationDateRepository = new JdbcReservationDateRepository(jdbcTemplate);
        reservationTimeRepository = new JdbcReservationTimeRepository(jdbcTemplate);
        themeRepository = new JdbcThemeRepository(jdbcTemplate);
    }

    @Test
    @DisplayName("예약을 저장한다.")
    void save_reservation() {
        // given
        String name = "이산";
        ReservationDate reservationDate = ReservationDate.createWithoutId(LocalDate.now());
        ReservationDate savedReservationDate = reservationDateRepository.save(reservationDate);
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(11, 0));
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        Theme theme = Theme.createWithoutId("테마", "테마 내용", "/themes/theme");
        Theme savedTheme = themeRepository.save(theme);
        Reservation reservation = Reservation.createWithoutId(name, savedReservationDate, savedReservationTime,
            savedTheme);

        // when
        Reservation save = reservationRepository.save(reservation);

        // then
        assertThat(save.getName()).isEqualTo(name);
        assertThat(save.getDate().getPlayDay()).isEqualTo(savedReservationDate.getPlayDay());
        assertThat(save.getTime().getStartAt()).isEqualTo(savedReservationTime.getStartAt());
        assertThat(save.getTheme().getName()).isEqualTo(savedTheme.getName());
        assertThat(save.getTheme().getContent()).isEqualTo(savedTheme.getContent());
        assertThat(save.getTheme().getUrl()).isEqualTo(savedTheme.getUrl());
    }

    @Test
    @DisplayName("예약을 아이디로 조회한다.")
    void find_reservation_by_id() {
        // given
        String name = "이산";
        ReservationDate reservationDate = ReservationDate.createWithoutId(LocalDate.now());
        ReservationDate savedReservationDate = reservationDateRepository.save(reservationDate);
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(11, 0));
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        Theme theme = Theme.createWithoutId("테마", "테마 내용", "/themes/theme");
        Theme savedTheme = themeRepository.save(theme);
        Reservation reservation = Reservation.createWithoutId(name, savedReservationDate, savedReservationTime,
            savedTheme);
        Reservation save = reservationRepository.save(reservation);

        // when
        Optional<Reservation> findReservation = reservationRepository.findById(save.getId());

        // then
        assertThat(findReservation).isPresent();
        Reservation actual = findReservation.get();
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getDate().getPlayDay()).isEqualTo(savedReservationDate.getPlayDay());
        assertThat(actual.getTime().getStartAt()).isEqualTo(savedReservationTime.getStartAt());
        assertThat(actual.getTheme().getName()).isEqualTo(savedTheme.getName());
        assertThat(actual.getTheme().getContent()).isEqualTo(savedTheme.getContent());
        assertThat(actual.getTheme().getUrl()).isEqualTo(savedTheme.getUrl());
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 조회하면 빈 Optional을 반환한다.")
    void find_reservation_by_id_not_exist() {
        // given
        Long id = 9999L;

        // when
        Optional<Reservation> findReservation = reservationRepository.findById(id);

        // then
        assertThat(findReservation).isEmpty();
    }

    @Test
    @Sql("/reservation.sql")
    @DisplayName("예약을 조회한다.")
    void find_all_reservation() {
        // given & when
        List<Reservation> reservations = reservationRepository.findAll();

        // then
        assertThat(reservations).hasSize(78);
    }

    @Test
    @DisplayName("예약을 아이디로 삭제한다.")
    void delete_reservation_by_id() {
        // given
        String name = "이산";
        ReservationDate reservationDate = ReservationDate.createWithoutId(LocalDate.now());
        ReservationDate savedReservationDate = reservationDateRepository.save(reservationDate);
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(11, 0));
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        Theme theme = Theme.createWithoutId("테마", "테마 내용", "/themes/theme");
        Theme savedTheme = themeRepository.save(theme);
        Reservation reservation = Reservation.createWithoutId(name, savedReservationDate, savedReservationTime,
            savedTheme);
        Reservation save = reservationRepository.save(reservation);

        // when
        reservationRepository.deleteById(save.getId());

        // then
        assertThat(reservationRepository.findById(save.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 삭제하면 0을 반환한다.")
    void delete_reservation_by_id_not_exist() {
        // given
        Long id = 9999L;

        // when
        int deleteCount = reservationRepository.deleteById(id);

        // then
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    @Sql("/reservation.sql")
    @DisplayName("시간 아이디를 통해 예약 개수를 확인한다.")
    void check_reservation_by_time_id() {
        // given & when
        int count = reservationRepository.countByTimeId(1L);

        // then
        assertThat(count).isEqualTo(19);
    }

    @Test
    @Sql("/reservation.sql")
    @DisplayName("날짜 아이디를 통해 예약 개수를 확인한다.")
    void check_reservation_by_date_id() {
        // given & when
        int count = reservationRepository.countByReservationDateId(1L);

        // then
        assertThat(count).isEqualTo(14);
    }

    @Test
    @Sql("/reservation.sql")
    @DisplayName("테마 아이디를 통해 예약 개수를 확인한다.")
    void check_reservation_by_theme_id() {
        // given & when
        int count = reservationRepository.countByThemeId(1L);

        // then
        assertThat(count).isEqualTo(12);
    }

    @Test
    @Sql("/reservation.sql")
    @DisplayName("테마와 날짜에 따른 예약된 시간들을 조회한다.")
    void find_reserved_times() {
        // given & when
        List<Long> reservedTimes = reservationRepository.findReservedTimes(1L, 1L);

        // then
        assertThat(reservedTimes).hasSize(4);
    }

    @Test
    @Sql("/reservation.sql")
    @DisplayName("예약자명으로 예약을 조회한다.")
    void find_reservation_by_name() {
        // given & when
        List<Reservation> reservations = reservationRepository.findByName("이산");

        // then
        assertThat(reservations).hasSize(1);
    }

    @Test
    @DisplayName("예약을 수정한다.")
    void update_reservation() {
        // given
        String name = "이산";
        ReservationDate reservationDate = ReservationDate.createWithoutId(LocalDate.now().plusDays(1));
        ReservationDate savedReservationDate = reservationDateRepository.save(reservationDate);
        ReservationDate updateDate = ReservationDate.createWithoutId(LocalDate.now().plusDays(2));
        ReservationDate savedUpdateDate = reservationDateRepository.save(updateDate);
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(11, 0));
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        ReservationTime updateTime = ReservationTime.createWithoutId(LocalTime.of(12, 0));
        ReservationTime savedUpdateTime = reservationTimeRepository.save(updateTime);
        Theme theme = Theme.createWithoutId("테마", "테마 내용", "/themes/theme");
        Theme savedTheme = themeRepository.save(theme);
        Reservation reservation = Reservation.createWithoutId(name, savedReservationDate, savedReservationTime,
            savedTheme);
        Reservation save = reservationRepository.save(reservation);

        // when
        reservationRepository.updateReservation(save.getId(), savedUpdateDate.getId(), savedUpdateTime.getId());
        Reservation findReservation = reservationRepository.findById(save.getId()).get();

        // then
        assertThat(findReservation.getDate().getPlayDay()).isEqualTo(savedUpdateDate.getPlayDay());
        assertThat(findReservation.getTime().getStartAt()).isEqualTo(savedUpdateTime.getStartAt());
    }

    @Test
    @Sql("/reservation.sql")
    @DisplayName("날짜, 시간, 테마 아이디를 통해 예약이 존재하는지 확인한다.")
    void check_reservation_by_date_time_theme_id() {
        // given & then
        boolean exist = reservationRepository.existsByDateIdAndTimeIdAndThemeId(1L, 1L, 1L);
        boolean notExist = reservationRepository.existsByDateIdAndTimeIdAndThemeId(1L, 2L, 2L);

        // then
        assertThat(exist).isTrue();
        assertThat(notExist).isFalse();
    }
}
