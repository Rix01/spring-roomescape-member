package roomescape.domain.theme;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import roomescape.support.exception.RoomescapeException;

class ThemeTest {

    @DisplayName("성공 케이스")
    @Nested
    class Success {

        @Test
        @DisplayName("테마를 생성할 수 있다.")
        void create_theme() {
            // given
            Long id = 1L;
            String name = "테스트 테마";
            String content = "테마 내용";
            String url = "/themes/test-theme";

            // when & then
            assertThatCode(() -> {
                Theme theme = Theme.of(id, name, content, url);
            })
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("아이디 없이 테마를 생성할 수 있다.")
        void create_theme_without_id() {
            // given
            String name = "테스트 테마";
            String content = "테마 내용";
            String url = "/themes/test-theme";

            // when & then
            assertThatCode(() -> {
                Theme theme = Theme.createWithoutId(name, content, url);
            })
                .doesNotThrowAnyException();
        }
    }

    @DisplayName("실패 케이스")
    @Nested
    class Fail {

        @Test
        @DisplayName("이름이 null이면 예외가 발생한다.")
        void exception_when_name_null() {
            // given
            Long id = 1L;
            String name = null;
            String content = "테마 내용";
            String url = "/themes/test-theme";

            // when & then
            assertThatThrownBy(() -> {
                Theme theme = Theme.of(id, name, content, url);
            })
                .isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("테마 명칭 데이터가 유효하지 않습니다.");
        }

        @Test
        @DisplayName("내용이 null이면 예외가 발생한다.")
        void exception_when_content_null() {
            // given
            Long id = 1L;
            String name = "테마";
            String content = null;
            String url = "/themes/test-theme";

            // when & then
            assertThatThrownBy(() -> {
                Theme theme = Theme.of(id, name, content, url);
            })
                .isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("테마 설명 데이터가 유효하지 않습니다.");
        }

        @Test
        @DisplayName("url이 null이면 예외가 발생한다.")
        void exception_when_url_null() {
            // given
            Long id = 1L;
            String name = "테마";
            String content = "테마 내용";
            String url = null;

            // when & then
            assertThatThrownBy(() -> {
                Theme theme = Theme.of(id, name, content, url);
            })
                .isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("테마 포스터 URL 데이터가 유효하지 않거나 형식이 올바르지 않습니다.");
        }
    }
}
