package roomescape.domain.theme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.dto.AdminThemeResponse;
import roomescape.domain.theme.dto.ThemeCreationRequest;
import roomescape.domain.theme.dto.ThemeCreationResponse;
import roomescape.domain.theme.dto.ThemeRankResponse;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.support.exception.RoomescapeException;
import roomescape.support.exception.ThemeErrorCode;

@Transactional
@SpringBootTest
@Sql("/truncate.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class ThemeServiceTest {

    @Autowired
    private ThemeService themeService;

    @DisplayName("성공 케이스")
    @Nested
    class Success {

        @Test
        @DisplayName("새로운 테마를 생성한다.")
        void createTheme() {
            // given
            ThemeCreationRequest request = new ThemeCreationRequest("새로운 테마", "새로운 설명", "new.jpg");

            // when
            ThemeCreationResponse response = themeService.createTheme(request);

            // then
            assertThat(response.name()).isEqualTo("새로운 테마");
            assertThat(response.content()).isEqualTo("새로운 설명");
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("사용자용 전체 테마 목록을 조회한다.")
        void getAllTheme() {
            // when
            List<ThemeResponse> responses = themeService.getAllTheme();

            // then
            assertThat(responses).hasSize(12);
            assertThat(responses).extracting(ThemeResponse::name)
                .contains("정조 대왕의 비밀", "이순신의 한산도");
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("관리자용 전체 테마 목록을 조회한다.")
        void getAllThemeForAdmin() {
            // when
            List<AdminThemeResponse> responses = themeService.getAllThemeForAdmin();

            // then
            assertThat(responses).hasSize(12);
            assertThat(responses).extracting(AdminThemeResponse::name)
                .contains("정조 대왕의 비밀", "이순신의 한산도");
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("인기 테마 랭킹을 조회한다.")
        void getThemeRank() {
            // when
            List<ThemeRankResponse> responses = themeService.getThemeRank();

            // then
            assertThat(responses).hasSize(10);
            assertThat(responses.get(0).name()).isEqualTo("정조 대왕의 비밀");
            assertThat(responses.get(1).name()).isEqualTo("이순신의 한산도");
            assertThat(responses).extracting(ThemeRankResponse::name)
                .doesNotContain("테마11", "테마12");
        }

        @Test
        @Sql("/reservation.sql")
        @DisplayName("ID로 테마를 조회한다.")
        void findById() {
            // when
            Theme theme = themeService.findById(1L);

            // then
            assertThat(theme.getName()).isEqualTo("정조 대왕의 비밀");
        }

        @Test
        @DisplayName("예약이 없는 테마를 삭제한다.")
        void deleteTheme() {
            // given
            ThemeCreationResponse created = themeService.createTheme(new ThemeCreationRequest("삭제될 테마", "설명", "url"));

            // when
            themeService.deleteTheme(created.id());

            // then
            List<ThemeResponse> allThemes = themeService.getAllTheme();
            assertThat(allThemes).isEmpty();
        }
    }

    @DisplayName("실패 케이스")
    @Nested
    class Fail {

        @Test
        @Sql("/reservation.sql")
        @DisplayName("예약이 존재하는 테마를 삭제하면 예외가 발생한다.")
        void deleteThemeInUse() {
            // when & then
            assertThatThrownBy(() -> themeService.deleteTheme(1L))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ThemeErrorCode.THEME_IN_USE);
        }

        @Test
        @DisplayName("존재하지 않는 ID의 테마를 삭제해도 예외는 발생하지 않는다.")
        void deleteThemeNotFound() {
            // when & then
            themeService.deleteTheme(999L);
        }

        @Test
        @DisplayName("존재하지 않는 ID의 테마를 조회하면 예외가 발생한다.")
        void findByIdNotFound() {
            // when & then
            assertThatThrownBy(() -> themeService.findById(999L))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(ThemeErrorCode.THEME_NOT_EXIST);
        }
    }
}
