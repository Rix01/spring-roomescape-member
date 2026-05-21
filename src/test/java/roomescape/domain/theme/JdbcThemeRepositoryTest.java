package roomescape.domain.theme;

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
import org.springframework.test.context.jdbc.SqlMergeMode;

@JdbcTest
@Sql("/truncate.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Import(JdbcThemeRepository.class)
class JdbcThemeRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    @DisplayName("테마를 저장한다.")
    void save_theme() {
        // given
        Theme theme = Theme.createWithoutId("테마", "테마 내용", "/themes/theme");

        // when
        Theme savedTheme = themeRepository.save(theme);

        // then
        assertThat(theme.getName()).isEqualTo(savedTheme.getName());
        assertThat(theme.getContent()).isEqualTo(savedTheme.getContent());
        assertThat(theme.getUrl()).isEqualTo(savedTheme.getUrl());
    }

    @Test
    @DisplayName("테마를 아이디로 조회한다.")
    void find_theme_by_id() {
        // given
        Theme theme = Theme.createWithoutId("테마", "테마 내용", "/themes/theme");
        Theme savedTheme = themeRepository.save(theme);

        // when
        Optional<Theme> findTheme = themeRepository.findById(savedTheme.getId());

        // then
        assertThat(findTheme).isPresent();
        Theme actual = findTheme.get();
        assertThat(actual.getId()).isEqualTo(savedTheme.getId());
        assertThat(actual.getName()).isEqualTo(savedTheme.getName());
        assertThat(actual.getContent()).isEqualTo(savedTheme.getContent());
        assertThat(actual.getUrl()).isEqualTo(savedTheme.getUrl());
    }

    @Test
    @DisplayName("테마를 조회한다.")
    void find_all_theme() {
        // given
        Theme theme = Theme.createWithoutId("테마1", "테마 내용1", "/themes/theme1");
        themeRepository.save(theme);
        Theme theme2 = Theme.createWithoutId("테마2", "테마 내용2", "/themes/theme2");
        themeRepository.save(theme2);

        // when
        List<Theme> themes = themeRepository.findAll();

        // then
        assertThat(themes).hasSize(2);
    }

    @Test
    @DisplayName("아이디로 테마를 삭제한다.")
    void delete_theme_by_id() {
        // given
        Theme theme = Theme.createWithoutId("테마", "테마 내용", "/themes/theme");
        Theme savedTheme = themeRepository.save(theme);

        // when
        themeRepository.deleteById(savedTheme.getId());

        // then
        assertThat(themeRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 조회하면 빈 Optional을 반환한다.")
    void find_theme_by_id_not_exist() {
        // given
        Long notExistId = 9999L;

        // when
        Optional<Theme> findTheme = themeRepository.findById(notExistId);

        // then
        assertThat(findTheme).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 삭제하면 0을 반환한다.")
    void delete_theme_by_id_not_exist() {
        // given
        Long notExistId = 9999L;

        // when
        int deleteCount = themeRepository.deleteById(notExistId);

        // then
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    @Sql("/popular-themes.sql")
    @DisplayName("일주일간의 인기 테마 top10을 조회한다.")
    void find_popular_themes_top10() {
        // given
        int rankLimit = 10;
        LocalDate today = LocalDate.now();
        LocalDate startDay = today.minusDays(7);
        LocalDate endDay = today.minusDays(1);

        // when
        List<Theme> top10Themes = themeRepository.findPopularThemes(rankLimit, startDay, endDay);

        // then
        assertThat(top10Themes).hasSize(rankLimit);
    }
}
