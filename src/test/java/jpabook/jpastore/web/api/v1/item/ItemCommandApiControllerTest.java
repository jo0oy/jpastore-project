package jpabook.jpastore.web.api.v1.item;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.domain.item.Book;
import jpabook.jpastore.domain.item.ItemRepository;
import jpabook.jpastore.security.JwtTokenProvider;
import jpabook.jpastore.web.dto.item.ItemDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@DisplayName("상품 등록/수정/삭제 API 테스트")
@Slf4j
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemCommandApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String userToken;

    private String adminToken;

    @BeforeEach
    void setUp() {
        userToken = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("member1", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))).getAccessToken();

        adminToken = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))).getAccessToken();
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.cleanUp();
        userToken = null;
        adminToken = null;
    }

    @DisplayName("[성공][api] 책 상품 등록")
    @Test
    void givenBookItemRegisterReq_whenPostNewItem_thenSaveNewBookItem() {
        //given
        var url = "http://localhost:" + port + "/api/v1/items/book";
        var requestDto = ItemDto.BookItemRegisterReq.builder()
                .author("지은이1")
                .name("새로운 책입니다.")
                .categoryId(6L)
                .isbn("12345")
                .stockQuantity(100)
                .price(15000)
                .build();

        //when & then
        this.webTestClient
                .post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.data.registeredItemId").isEqualTo(10);
    }

    @DisplayName("[실패][api] 새로운 책 상품 등록 - 일반 회원")
    @Test
    void givenBookItemRegisterReqWithUserToken_whenPostNewItem_thenReturnForbiddenError() {
        //given
        var url = "http://localhost:" + port + "/api/v1/items/book";
        var requestDto = ItemDto.BookItemRegisterReq.builder()
                .author("지은이1")
                .name("새로운 책입니다.")
                .categoryId(6L)
                .isbn("12345")
                .stockQuantity(100)
                .price(15000)
                .build();

        //when & then
        this.webTestClient
                .post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().jsonPath("$.error.ex").isEqualTo("AccessDeniedException");
    }

    @DisplayName("[실패][api] 책 상품 등록 - 올바르지 않은 요청 데이터")
    @Test
    void givenInvalidBookItemRegisterReq_whenPostNewItem_thenReturnBadRequestError() {
        //given
        var url = "http://localhost:" + port + "/api/v1/items/book";
        var requestDto = ItemDto.BookItemRegisterReq.builder()
                .author("지은이1")
                .name("새로운 책입니다.")
                .categoryId(6L)
                .isbn("12345")
                .stockQuantity(100010)
                .price(15000)
                .build();

        //when & then
        this.webTestClient
                .post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error.ex").isEqualTo("not_valid_arg");
    }

    @DisplayName("[성공][api] 앨범 상품 등록")
    @Test
    void givenAlbumItemRegisterReq_whenPostNewItem_thenSaveNewAlbumItem() {
        //given
        var url = "http://localhost:" + port + "/api/v1/items/album";
        var requestDto = ItemDto.AlbumItemRegisterReq.builder()
                .name("새로운 앨범!!!")
                .artist("아티스트")
                .price(23000)
                .categoryId(12L)
                .stockQuantity(1000)
                .etc("새로운 앨범 발매됨")
                .build();

        //when & then
        this.webTestClient
                .post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.data.registeredItemId").isEqualTo(10);
    }

    @DisplayName("[성공][api] DVD 상품 등록")
    @Test
    void givenDvdItemRegisterReq_whenPostNewItem_thenSaveNewDvdItem() {
        //given
        var url = "http://localhost:" + port + "/api/v1/items/dvd";
        var requestDto = ItemDto.DvdItemRegisterReq.builder()
                .name("새로운 DVD 입니다.")
                .categoryId(11L)
                .actor("배우 여러명")
                .director("감독 이름")
                .stockQuantity(100)
                .price(34000)
                .build();

        //when & then
        this.webTestClient
                .post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.data.registeredItemId").isEqualTo(10);
    }

    @DisplayName("[성공][api] 책 상품 정보 수정")
    @Test
    void givenItemUpdateReq_whenPatchUpdateInfo_thenUpdateItemInfo() {
        //given
        var itemId = 1L;
        var updateName = "수정된 제목";
        var updateAuthor = "kimmy";
        var url = "http://localhost:" + port + "/api/v1/items/" + itemId;
        var requestDto = ItemDto.UpdateInfoReq.builder()
                .name(updateName)
                .author(updateAuthor)
                .build();

        //when & then
        this.webTestClient
                .patch()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.message").isEqualTo("상품 정보 수정 성공");

        var updatedItem = itemRepository.findById(itemId);
        assertThat(updatedItem).isPresent();
        assertThat(updatedItem.get().getName()).isEqualTo(updateName);
        assertThat(updatedItem.get()).isInstanceOf(Book.class);
        assertThat(((Book) updatedItem.get()).getAuthor()).isEqualTo(updateAuthor);
    }

    @DisplayName("[성공][api] 상품 정보 부분 업데이트 - 수정하고자 하는 상품 타입에 해당하지 않은 데이터를 전달")
    @Test
    void givenItemUpdateReqNotMatchType_whenPatchUpdateInfo_thenUpdateOnlyMatchedTypeData() {
        //given
        var itemId = 2L;
        var updateName = "수정된 제목의 책";
        var updatePrice = 20000;
        var updateDirector = "수정된 감독이름";
        var url = "http://localhost:" + port + "/api/v1/items/" + itemId;
        var requestDto = ItemDto.UpdateInfoReq.builder()
                .name(updateName)
                .price(updatePrice)
                .director(updateDirector)
                .build();

        //when & then
        this.webTestClient
                .patch()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.message").isEqualTo("상품 정보 수정 성공");

        var updatedItem = itemRepository.findById(itemId);
        assertThat(updatedItem).isPresent();
        assertThat(updatedItem.get().getName()).isEqualTo(updateName);
        assertThat(updatedItem.get().getPrice().getValue()).isEqualTo(updatePrice);
        assertThat(updatedItem.get()).isInstanceOf(Book.class);
    }

    @DisplayName("[실패][api] 업데이트 요청 데이터 검증 오류 - 검증기 정상 작동 확인")
    @Test
    void givenInvalidItemUpdateReq_whenPatchUpdateInfo_thenReturnBadRequestError() {
        //given
        var itemId = 2L;
        var updateName = "수정된 제목의 책";
        var updatePrice = 100;
        var updateStockQuantity = 11000;
        var url = "http://localhost:" + port + "/api/v1/items/" + itemId;
        var requestDto = ItemDto.UpdateInfoReq.builder()
                .name(updateName)
                .price(updatePrice)
                .stockQuantity(updateStockQuantity)
                .build();

        //when & then
        this.webTestClient
                .patch()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("not_valid_arg");
    }

    @DisplayName("[성공][api] 상품 삭제")
    @Test
    void givenItemId_whenDeleteItem_thenWorksFine() {
        //given
        var itemId = 5L;
        var url = "http://localhost:" + port + "/api/v1/items/" + itemId;

        //when & then
        this.webTestClient
                .delete()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @DisplayName("[실패][api] 상품 삭제 - 존재하지 않는 상품")
    @Test
    void givenItemIdThatNotExist_whenDeleteItem_thenReturnClientError() {
        //given
        var itemId = 100L;
        var url = "http://localhost:" + port + "/api/v1/items/" + itemId;

        //when & then
        this.webTestClient
                .delete()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.error.message", "존재하지 않는 상품입니다." + itemId);
    }

    @DisplayName("[실패][api] 상품 삭제 - 일반 회원")
    @Test
    void givenItemIdAndUserToken_whenDeleteItem_thenReturnForbiddenError() {
        //given
        var itemId = 3L;
        var url = "http://localhost:" + port + "/api/v1/items/" + itemId;

        //when & then
        this.webTestClient
                .delete()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.error.message", "접근이 거부되었습니다.");
    }
}
