-- 1. 부모 데이터: 테마 (Theme) 세팅 (ID: 1, 2)
INSERT INTO theme (id, name, content, url)
VALUES (1, '정조 대왕의 비밀', '조선 22대 왕 정조의 비밀 방탈출', '/themes/jeongjo');
INSERT INTO theme (id, name, content, url)
VALUES (2, '이순신의 한산도', '거북선을 구출하는 해전 방탈출', '/themes/sunsin');

-- 2. 부모 데이터: 예약 날짜 (ReservationDate) 세팅 (ID: 1, 2)
-- 2026-05-21(오늘), 2026-05-22(내일)
INSERT INTO reservation_date (id, play_day)
VALUES (1, '2026-05-21');
INSERT INTO reservation_date (id, play_day)
VALUES (2, '2026-05-22');

-- 3. 부모 데이터: 예약 시간 (ReservationTime) 세팅 (ID: 1, 2, 3)
-- 10:00, 14:00, 18:00
INSERT INTO reservation_time (id, start_at)
VALUES (1, '10:00');
INSERT INTO reservation_time (id, start_at)
VALUES (2, '14:00');
INSERT INTO reservation_time (id, start_at)
VALUES (3, '18:00');

-- 4. 자식 데이터: 예약 (Reservation) 세팅 (ID: 1, 2, 3)
-- [예약 1번] 이름: 이산, 날짜: 오늘(1), 시간: 10시(1), 테마: 정조(1)
INSERT INTO reservation (id, name, date_id, time_id, theme_id)
VALUES (1, '이산', 1, 1, 1);

-- [예약 2번] 이름: 홍국영, 날짜: 오늘(1), 시간: 14시(2), 테마: 정조(1)
-- 💡 의도: 오늘 날짜(1)와 정조 테마(1)에 예약이 '2개' 물리도록 세팅 (카운트 검증용)
INSERT INTO reservation (id, name, date_id, time_id, theme_id)
VALUES (2, '홍국영', 1, 2, 1);

-- [예약 3번] 이름: 이순신, 날짜: 내일(2), 시간: 18시(3), 테마: 이순신(2)
INSERT INTO reservation (id, name, date_id, time_id, theme_id)
VALUES (3, '이순신', 2, 3, 2);
