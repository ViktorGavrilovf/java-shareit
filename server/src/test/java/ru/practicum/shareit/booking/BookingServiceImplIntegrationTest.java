package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.dto.booking.BookingCreateDto;
import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.dto.booking.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({BookingServiceImpl.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setup() {
        owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@mail.com")
                .build());

        booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@mail.com")
                .build());

        item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .owner(owner)
                .build());

        booking = buildAndSaveBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);
    }

    @Test
    @Order(1)
    void testCreateBookingSuccessfully() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto bookingDto = bookingService.createBooking(booker.getId(), dto);

        assertThat(bookingDto.getId()).isNotNull();
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());

        Booking bookingFromDb = bookingRepository.findById(bookingDto.getId()).orElseThrow();
        assertThat(bookingFromDb).isNotNull();
    }

    @Test
    @Order(2)
    void testCreateBookingByOwnerShouldThrowAccessDenied() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(owner.getId(), dto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Нельзя забронировать свою собственную вещь");
    }

    @Test
    @Order(3)
    void testCreateBookingWithInvalidTimeShouldThrow() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(), dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("начала");
    }

    @Test
    @Order(4)
    void testCreateBookingUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();


        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(), dto))
                .isInstanceOf(AccessDeniedException.class);

        assertThrows(AccessDeniedException.class, () -> bookingService.createBooking(owner.getId(), dto));
    }

    @Test
    @Order(5)
    void testApproveBooking() {
        BookingDto approved = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(approved.getId()).isEqualTo(booking.getId());
    }

    @Test
    @Order(8)
    void testGetBookingByOwner() {
        BookingDto result = bookingService.getBooking(owner.getId(), booking.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    @Order(9)
    void testGetBookingByBooker() {
        BookingDto result = bookingService.getBooking(booker.getId(), booking.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    @Order(13)
    void testGetBookingsForUser_AllStates() {
        buildAndSaveBooking(
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED
        );

        buildAndSaveBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                BookingStatus.APPROVED
        );

        buildAndSaveBooking(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED
        );

        buildAndSaveBooking(
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(8),
                BookingStatus.REJECTED
        );

        List<BookingDto> all = bookingService.getBookingsForUser(booker.getId(), "ALL");
        assertThat(all).hasSize(5);

        List<BookingDto> past = bookingService.getBookingsForUser(booker.getId(), "PAST");
        assertThat(past).hasSize(1);

        List<BookingDto> future = bookingService.getBookingsForUser(booker.getId(), "FUTURE");
        assertThat(future).hasSize(3);

        List<BookingDto> current = bookingService.getBookingsForUser(booker.getId(), "CURRENT");
        assertThat(current).hasSize(1);

        List<BookingDto> waiting = bookingService.getBookingsForUser(booker.getId(), "WAITING");
        assertThat(waiting).hasSize(1);
        assertThat(waiting.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);

        List<BookingDto> rejected = bookingService.getBookingsForUser(booker.getId(), "REJECTED");
        assertThat(rejected).hasSize(1);
        assertThat(rejected.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    @Order(14)
    void testGetBookingsForOwner_AllStates() {
        buildAndSaveBooking(
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED
        );

        buildAndSaveBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                BookingStatus.APPROVED
        );

        buildAndSaveBooking(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED
        );

        buildAndSaveBooking(
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(8),
                BookingStatus.REJECTED
        );

        List<BookingDto> all = bookingService.getBookingsForOwner(owner.getId(), "ALL");
        assertThat(all).hasSize(5);

        List<BookingDto> past = bookingService.getBookingsForOwner(owner.getId(), "PAST");
        assertThat(past).hasSize(1);

        List<BookingDto> future = bookingService.getBookingsForOwner(owner.getId(), "FUTURE");
        assertThat(future).hasSize(3);

        List<BookingDto> current = bookingService.getBookingsForOwner(owner.getId(), "CURRENT");
        assertThat(current).hasSize(1);

        List<BookingDto> waiting = bookingService.getBookingsForOwner(owner.getId(), "WAITING");
        assertThat(waiting).hasSize(1);
        assertThat(waiting.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);

        List<BookingDto> rejected = bookingService.getBookingsForOwner(owner.getId(), "REJECTED");
        assertThat(rejected).hasSize(1);
        assertThat(rejected.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    private Booking buildAndSaveBooking(LocalDateTime start, LocalDateTime end, BookingStatus status) {
        return bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .status(status)
                .build());
    }
}
