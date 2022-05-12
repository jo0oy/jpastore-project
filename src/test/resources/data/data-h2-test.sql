INSERT INTO item (name, price, stock_quantity, author, isbn, dtype, item_id)
VALUES ('book1', 10000, 100, 'kim1', '11111', 'Book', 1);

INSERT INTO item (name, price, stock_quantity, author, isbn, dtype, item_id)
VALUES ('book2', 15000, 50, 'kim2', '22222', 'Book', 2);

INSERT INTO item (name, price, stock_quantity, author, isbn, dtype, item_id)
VALUES ('book3', 17500, 75, 'kim3', '33333', 'Book', 3);

INSERT INTO item (name, price, stock_quantity, artist, etc, dtype, item_id)
VALUES ('album1', 21000, 150, 'artist1', 'artist1 mini', 'Album', 4);

INSERT INTO item (name, price, stock_quantity, artist, etc, dtype, item_id)
VALUES ('album2', 19500, 200, 'artist2', 'artist2 full', 'Album', 5);

INSERT INTO item (name, price, stock_quantity, artist, etc, dtype, item_id)
VALUES ('album3', 25000, 300, 'artist3', 'artist3 repackage', 'Album', 6);

INSERT INTO item (name, price, stock_quantity, artist, etc, dtype, item_id)
VALUES ('album4', 23500, 250, 'artist4', 'artist4 full', 'Album', 7);

INSERT INTO item (name, price, stock_quantity, director, actor, dtype, item_id)
VALUES ('movie1', 27000, 400, 'director1', 'actor1, actor2', 'Dvd', 8);

INSERT INTO item (name, price, stock_quantity, director, actor, dtype, item_id)
VALUES ('movie2', 30000, 450, 'director2', 'actor3, actor4', 'Dvd', 9);

INSERT INTO membership (grade, total_spending, membership_id)
VALUES ('SILVER', 250000, 1);

INSERT INTO membership (grade, total_spending, membership_id)
VALUES ('VIP', 170000, 2);

INSERT INTO membership (grade, total_spending, membership_id)
VALUES ('GOLD', 399000, 3);

INSERT INTO membership (grade, total_spending, membership_id)
VALUES ('GOLD', 450000, 4);

INSERT INTO membership (grade, total_spending, membership_id)
VALUES ('SILVER', 50000, 5);

INSERT INTO member (name, city, street, zipcode, member_id, membership_id)
VALUES ('member1', '서울시', '송파구', '1111', 1, 1);

INSERT INTO member (name, city, street, zipcode, member_id, membership_id)
VALUES ('member2', '서울시', '성북구', '2222', 2, 2);

INSERT INTO member (name, city, street, zipcode, member_id, membership_id)
VALUES ('member3', '경기도', '판교시', '3333', 3, 3);

INSERT INTO member (name, city, street, zipcode, member_id, membership_id)
VALUES ('member4', '경기도', '판교시', '3333', 4, 4);

INSERT INTO member (name, city, street, zipcode, member_id, membership_id)
VALUES ('member5', '경기도', '분당시', '4444', 5, 5);



INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '성북구', '2222', 'READY', 1);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '송파구', '1111', 'READY', 2);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '송파구', '1111', 'NONE', 3);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '강남구', '3333', 'PREPARING', 4);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '성동구', '4444', 'DELIVERING', 5);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('경기도', '판교시', '5555', 'READY', 6);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('경기도', '강남구', '3333', 'DELIVERING', 7);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '성동구', '4444', 'DELIVERING', 8);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('경기도', '판교시', '5555', 'COMPLETE', 9);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '성북구', '2222', 'NONE', 10);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '강남구', '3333', 'PREPARING', 11);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '성북구', '2222', 'READY', 12);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('경기도', '판교시', '5555', 'DELIVERING', 13);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '송파구', '1111', 'NONE', 14);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '성동구', '4444', 'COMPLETE', 15);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('경기도', '판교시', '5555', 'PREPARING', 16);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '송파구', '1111', 'DELIVERING', 17);


INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (2, 1, 'ORDER', 'CARD', 1);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (1, 2, 'ORDER', 'NAVER_PAY', 2);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (1, 3, 'PAYMENT_WAITING', 'BANK_TRANS', 3);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (3, 4, 'ORDER', 'COUPON', 4);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (2, 5, 'ORDER', 'KAKAO_PAY', 5);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (4, 6, 'ORDER', 'CARD', 6);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (5, 7, 'ORDER', 'KAKAO_PAY', 7);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (3, 8, 'ORDER', 'INSTANT_CASH', 8);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (2, 9, 'ORDER', 'NAVER_PAY', 9);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (4, 10, 'PAYMENT_WAITING', 'BANK_TRANS', 10);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (5, 11, 'ORDER', 'INSTANT_CASH', 11);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (4, 12, 'ORDER', 'CARD', 12);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (3, 13, 'ORDER', 'KAKAO_PAY', 13);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (1, 14, 'PAYMENT_WAITING', 'BANK_TRANS', 14);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (2, 15, 'ORDER', 'CARD', 15);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (5, 16, 'ORDER', 'INSTANT_CASH', 16);

INSERT INTO orders (member_id, delivery_id, status, pay_info, order_id)
VALUES (3, 17, 'ORDER', 'NAVER_PAY', 17);



INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (1, 1, 10000, 5, 1);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (3, 1, 16000, 10, 2);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (2, 2, 15000, 3, 3);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (1, 2, 9900, 7, 4);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (3, 2, 17500, 2, 5);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (4, 3, 17000, 2, 6);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (5, 3, 20000, 1, 7);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (2, 4, 15000, 3, 8);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (6, 4, 24000, 2, 9);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (8, 5, 24500, 2, 10);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (9, 6, 23000, 1, 11);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (2, 7, 13500, 2, 12);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (7, 7, 23000, 5, 13);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (5, 8, 20000, 2, 14);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (1, 9, 10000, 3, 15);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (3, 10, 17000, 1, 16);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (4, 10, 18000, 1, 17);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (2, 11, 15000, 2, 18);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (8, 11, 24000, 1, 19);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (9, 11, 23000, 1, 20);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (6, 12, 24000, 3, 21);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (5, 13, 20000, 3, 22);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (7, 13, 22500, 5, 23);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (3, 14, 17000, 2, 24);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (4, 15, 20500, 1, 25);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (5, 15, 19500, 2, 26);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (6, 15, 23000, 1, 27);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (7, 15, 23000, 1, 28);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (1, 16, 9900, 2, 29);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (2, 16, 15000, 2, 30);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (9, 17, 27000, 2, 31);


