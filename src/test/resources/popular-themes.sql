-- 1. 테마 데이터 (12개)
INSERT INTO theme (name, content, url) VALUES ('테마1', '설명1', 'url1');
INSERT INTO theme (name, content, url) VALUES ('테마2', '설명2', 'url2');
INSERT INTO theme (name, content, url) VALUES ('테마3', '설명3', 'url3');
INSERT INTO theme (name, content, url) VALUES ('테마4', '설명4', 'url4');
INSERT INTO theme (name, content, url) VALUES ('테마5', '설명5', 'url5');
INSERT INTO theme (name, content, url) VALUES ('테마6', '설명6', 'url6');
INSERT INTO theme (name, content, url) VALUES ('테마7', '설명7', 'url7');
INSERT INTO theme (name, content, url) VALUES ('테마8', '설명8', 'url8');
INSERT INTO theme (name, content, url) VALUES ('테마9', '설명9', 'url9');
INSERT INTO theme (name, content, url) VALUES ('테마10', '설명10', 'url10');
INSERT INTO theme (name, content, url) VALUES ('테마11', '설명11', 'url11');
INSERT INTO theme (name, content, url) VALUES ('테마12', '설명12', 'url12');

-- 2. 예약 날짜 데이터 (오늘 기준 1일 전 ~ 7일 전)
INSERT INTO reservation_date (play_day) VALUES (CURRENT_DATE - 1);
INSERT INTO reservation_date (play_day) VALUES (CURRENT_DATE - 2);
INSERT INTO reservation_date (play_day) VALUES (CURRENT_DATE - 3);
INSERT INTO reservation_date (play_day) VALUES (CURRENT_DATE - 4);
INSERT INTO reservation_date (play_day) VALUES (CURRENT_DATE - 5);
INSERT INTO reservation_date (play_day) VALUES (CURRENT_DATE - 6);
INSERT INTO reservation_date (play_day) VALUES (CURRENT_DATE - 7);

-- 3. 예약 시간 데이터
INSERT INTO reservation_time (start_at) VALUES ('10:00');
INSERT INTO reservation_time (start_at) VALUES ('12:00');
INSERT INTO reservation_time (start_at) VALUES ('14:00');
INSERT INTO reservation_time (start_at) VALUES ('16:00');
INSERT INTO reservation_time (start_at) VALUES ('18:00');
INSERT INTO reservation_time (start_at) VALUES ('20:00');
INSERT INTO reservation_time (start_at) VALUES ('22:00');

-- 4. 예약 데이터 삽입 (테마별로 예약 건수를 다르게 하여 순위 조작)
INSERT INTO reservation (name, date_id, time_id, theme_id) VALUES
-- 테마1 (12건)
('u1', 1, 1, 1), ('u2', 1, 2, 1), ('u3', 1, 3, 1), ('u4', 1, 4, 1), ('u5', 2, 1, 1), ('u6', 2, 2, 1),
('u7', 2, 3, 1), ('u8', 3, 1, 1), ('u9', 3, 2, 1), ('u10', 4, 1, 1), ('u11', 5, 1, 1), ('u12', 6, 1, 1),

-- 테마2 (11건)
('u1', 1, 5, 2), ('u2', 1, 6, 2), ('u3', 2, 4, 2), ('u4', 2, 5, 2), ('u5', 3, 3, 2), ('u6', 3, 4, 2),
('u7', 4, 2, 2), ('u8', 4, 3, 2), ('u9', 5, 2, 2), ('u10', 6, 2, 2), ('u11', 7, 1, 2),

-- 테마3 (10건)
('u1', 1, 7, 3), ('u2', 2, 6, 3), ('u3', 2, 7, 3), ('u4', 3, 5, 3), ('u5', 3, 6, 3), ('u6', 4, 4, 3),
('u7', 4, 5, 3), ('u8', 5, 3, 3), ('u9', 6, 3, 3), ('u10', 7, 2, 3),

-- 테마4 (9건)
('u1', 2, 1, 4), ('u2', 2, 2, 4), ('u3', 3, 1, 4), ('u4', 3, 2, 4), ('u5', 4, 1, 4), ('u6', 4, 2, 4),
('u7', 5, 1, 4), ('u8', 6, 1, 4), ('u9', 7, 3, 4),

-- 테마5 (8건)
('u1', 1, 1, 5), ('u2', 2, 1, 5), ('u3', 3, 1, 5), ('u4', 4, 1, 5), ('u5', 5, 1, 5), ('u6', 6, 1, 5),
('u7', 7, 1, 5), ('u8', 7, 4, 5),

-- 테마6 (7건)
('u1', 1, 2, 6), ('u2', 2, 2, 6), ('u3', 3, 2, 6), ('u4', 4, 2, 6), ('u5', 5, 2, 6), ('u6', 6, 2, 6),
('u7', 7, 2, 6),

-- 테마7 (6건)
('u1', 1, 3, 7), ('u2', 2, 3, 7), ('u3', 3, 3, 7), ('u4', 4, 3, 7), ('u5', 5, 3, 7), ('u6', 6, 3, 7),

-- 테마8 (5건)
('u1', 1, 4, 8), ('u2', 2, 4, 8), ('u3', 3, 4, 8), ('u4', 4, 4, 8), ('u5', 5, 4, 8),

-- 테마9 (4건)
('u1', 1, 5, 9), ('u2', 2, 5, 9), ('u3', 3, 5, 9), ('u4', 4, 5, 9),

-- 테마10 (3건)
('u1', 1, 6, 10), ('u2', 2, 6, 10), ('u3', 3, 6, 10),

-- 테마11 (2건) - Top 10에 들지 못함
('u1', 1, 7, 11), ('u2', 2, 7, 11),

-- 테마12 (1건) - Top 10에 들지 못함
('u1', 3, 7, 12);
