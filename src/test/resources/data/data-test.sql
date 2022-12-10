INSERT INTO items (name, price, stock_quantity, author, isbn, dtype, is_deleted)
VALUES ('book1', 10000, 100, 'kim1', '11111', 'Book', false);

INSERT INTO items (name, price, stock_quantity, author, isbn, dtype, is_deleted)
VALUES ('book2', 15000, 50, 'kim2', '22222', 'Book', false);

INSERT INTO items (name, price, stock_quantity, author, isbn, dtype, is_deleted)
VALUES ('book3', 17500, 75, 'kim3', '33333', 'Book', false);

INSERT INTO items (name, price, stock_quantity, artist, etc, dtype, is_deleted)
VALUES ('album1', 21000, 150, 'artist1', 'artist1 mini', 'Album', false);

INSERT INTO items (name, price, stock_quantity, artist, etc, dtype, is_deleted)
VALUES ('album2', 19500, 200, 'artist2', 'artist2 full', 'Album', false);

INSERT INTO items (name, price, stock_quantity, artist, etc, dtype, is_deleted)
VALUES ('album3', 25000, 300, 'artist3', 'artist3 repackage', 'Album', false);

INSERT INTO items (name, price, stock_quantity, artist, etc, dtype, is_deleted)
VALUES ('album4', 23500, 250, 'artist4', 'artist4 full', 'Album', false);

INSERT INTO items (name, price, stock_quantity, artist, etc, dtype, is_deleted)
VALUES ('movie1', 27000, 400, 'director1', 'actor1, actor2', 'Dvd', false);

INSERT INTO items (name, price, stock_quantity, artist, etc, dtype, is_deleted)
VALUES ('movie2', 30000, 450, 'director2', 'actor3, actor4', 'Dvd', false);



INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (1, '도서', null, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (2, '음반', null, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (3, 'DVD', null, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (4, '국내도서', 1, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (5, '해외도서', 1, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (6, '국내소설', 4, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (7, '수필', 4, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (8, '해외소설', 5, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (9, '국내음반', 2, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (10, '해외음반', 2, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (11, '국내DVD', 3, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (12, 'KPOP', 9, false);

INSERT INTO categories (category_id, name, parent_id, is_deleted)
VALUES (13, 'POP', 10, false);


INSERT INTO category_items (category_item_id, category_id, item_id)
VALUES (1, 6, 1);

INSERT INTO category_items (category_item_id, category_id, item_id)
VALUES (2, 6, 2);

INSERT INTO category_items (category_item_id, category_id, item_id)
VALUES (3, 7, 3);

INSERT INTO category_items (category_item_id, category_id, item_id)
VALUES (4, 12, 4);

INSERT INTO category_items (category_item_id, category_id, item_id)
VALUES (5, 12, 5);

INSERT INTO category_items (category_item_id, category_id, item_id)
VALUES (6, 11, 8);



INSERT INTO memberships (membership_id, grade, total_spending, is_deleted)
VALUES (1, 'SILVER', 250000, false);

INSERT INTO memberships (membership_id, grade, total_spending, is_deleted)
VALUES (2, 'VIP', 170000, false);

INSERT INTO memberships (membership_id, grade, total_spending, is_deleted)
VALUES (3, 'GOLD', 399000, false);

INSERT INTO memberships (membership_id, grade, total_spending, is_deleted)
VALUES (4, 'GOLD', 450000, false);

INSERT INTO memberships (membership_id, grade, total_spending, is_deleted)
VALUES (5, 'SILVER', 0, false);


INSERT INTO members (member_id, username, password, city, street, zipcode, phone_number, email, role, membership_id, is_deleted)
VALUES (1, 'member1', '{noop}member1@pw', '서울시', '송파구', '1111', '010-1111-1111', 'member1@gmail.com', 'USER', 1, false);

INSERT INTO members (member_id, username, password, city, street, zipcode, phone_number, email, role, membership_id, is_deleted)
VALUES (2, 'member2', '{noop}member2@pw', '서울시', '성북구', '2222', '010-2222-2222', 'member2@naver.com', 'USER', 2, false);

INSERT INTO members (member_id, username, password, city, street, zipcode, phone_number, email, role, membership_id, is_deleted)
VALUES (3, 'member3', '{noop}member3@pw', '경기도', '판교시', '3333', '010-3333-3333', 'member3@gmail.com', 'USER', 3, false);

INSERT INTO members (member_id, username, password, city, street, zipcode, phone_number, email, role, membership_id, is_deleted)
VALUES (4, 'member4', '{noop}member4@pw', '경기도', '판교시', '3333', '010-4444-4444', 'member4@naver.com', 'USER', 4, false);

INSERT INTO members (member_id, username, password, city, street, zipcode, phone_number, email, role, membership_id, is_deleted)
VALUES (5, 'admin', '{noop}admin@pw', 'NONE', 'NONE', 'NONE', 'DEFAULT_VALUE', 'DEFAULT_VALUE', 'ADMIN', 5, false);



INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (1, '서울시', '성북구', '2222', 'READY');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (2, '서울시', '송파구', '1111', 'READY');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (3, '서울시', '송파구', '1111', 'NONE');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (4, '서울시', '강남구', '3333', 'PREPARING');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (5, '서울시', '성동구', '4444', 'DELIVERING');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (6, '경기도', '판교시', '5555', 'READY');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (7, '경기도', '강남구', '3333', 'DELIVERING');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (8, '서울시', '성동구', '4444', 'DELIVERING');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (9, '경기도', '판교시', '5555', 'COMPLETE');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (10, '서울시', '성북구', '2222', 'NONE');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (11, '서울시', '강남구', '3333', 'PREPARING');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (12, '서울시', '성북구', '2222', 'READY');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (13, '경기도', '판교시', '5555', 'DELIVERING');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (14, '서울시', '송파구', '1111', 'NONE');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (15, '서울시', '성동구', '4444', 'COMPLETE');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (16, '경기도', '판교시', '5555', 'PREPARING');

INSERT INTO deliveries (delivery_id, city, street, zipcode, status)
VALUES (17, '서울시', '송파구', '1111', 'DELIVERING');


INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (1, 2, 1, 'ORDER', 'CARD');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (2, 1, 2, 'ORDER', 'NAVER_PAY');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (3, 1, 3, 'PAYMENT_WAITING', 'BANK_TRANS');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (4, 3, 4, 'ORDER', 'COUPON');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (5, 2, 5, 'ORDER', 'KAKAO_PAY');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (6, 4, 6, 'ORDER', 'CARD');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (7, 5, 7, 'ORDER', 'KAKAO_PAY');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (8, 3, 8, 'ORDER', 'INSTANT_CASH');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (9, 2, 9, 'ORDER', 'NAVER_PAY');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (10, 4, 10, 'PAYMENT_WAITING', 'BANK_TRANS');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (11, 5, 11, 'ORDER', 'INSTANT_CASH');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (12, 4, 12, 'ORDER', 'CARD');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (13, 3, 13, 'ORDER', 'KAKAO_PAY');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (14, 1, 14, 'PAYMENT_WAITING', 'BANK_TRANS');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (15, 2, 15, 'ORDER', 'CARD');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (16, 5, 16, 'ORDER', 'INSTANT_CASH');

INSERT INTO orders (order_id, member_id, delivery_id, status, pay_info)
VALUES (17, 3, 17, 'ORDER', 'NAVER_PAY');



INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (1, 1, 1, 10000, 5);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (2, 3, 1, 16000, 10);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (3, 2, 2, 15000, 3);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (4, 1, 2, 9900, 7);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (5, 3, 2, 17500, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (6, 4, 3, 17000, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (7, 5, 3, 20000, 1);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (8, 2, 4, 15000, 3);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (9, 6, 4, 24000, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (10, 8, 5, 24500, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (11, 9, 6, 23000, 1);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (12, 2, 7, 13500, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (13, 7, 7, 23000, 5);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (14, 5, 8, 20000, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (15, 1, 9, 10000, 3);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (16, 3, 10, 17000, 1);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (17, 4, 10, 18000, 1);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (18, 2, 11, 15000, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (19, 8, 11, 24000, 1);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (20, 9, 11, 23000, 1);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (21, 6, 12, 24000, 3);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (22, 5, 13, 20000, 3);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (23, 7, 13, 22500, 5);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (24, 3, 14, 17000, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (25, 4, 15, 20500, 1);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (26, 5, 15, 19500, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (27, 6, 15, 23000, 1);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (28, 7, 15, 23000, 1);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (29, 1, 16, 9900, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (30, 2, 16, 15000, 2);

INSERT INTO order_items (order_item_id, item_id, order_id, order_price, quantity)
VALUES (31, 9, 17, 27000, 2);



INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (1, '리뷰1 입니다.', 1, 1, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (2, '리뷰2 입니다.', 1, 2, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (3, '리뷰3 입니다.', 3, 3, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (4, '리뷰4 입니다.', 3, 2, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (5, '리뷰5 입니다.', 3, 4, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (6, '리뷰6 입니다.', 4, 5, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (7, '리뷰7 입니다.', 4, 1, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (8, '리뷰8 입니다.', 6, 5, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (9, '리뷰9 입니다.', 6, 3, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (10, '리뷰10 입니다.', 6, 1, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (11, '리뷰11 입니다.', 6, 2, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (12, '리뷰12 입니다.', 8, 1, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (13, '리뷰13 입니다.', 8, 3, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (14, '리뷰14 입니다.', 5, 1, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (15, '리뷰15 입니다.', 7, 5, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (16, '리뷰16 입니다.', 9, 1, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (17, '리뷰17 입니다.', 9, 3, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (18, '리뷰18 입니다.', 2, 1, false);

INSERT INTO reviews (review_id, review_body, item_id, member_id, is_deleted)
VALUES (19, '리뷰19 입니다.', 2, 5, false);
