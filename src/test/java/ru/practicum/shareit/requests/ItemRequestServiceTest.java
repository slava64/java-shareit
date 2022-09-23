package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Test
    void testAdd() {
        UserDto userDto = makeUserDto("some@email.com", "Пётр");
        ItemRequestPostDto itemRequestPostDto = makeItemRequestPostDto("Description");

        userService.add(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        itemRequestService.add(user.getId(), itemRequestPostDto);

        TypedQuery<ItemRequest> queryItemRequest = em.createQuery(
                "Select ir from ItemRequest ir where ir.description = :description",
                ItemRequest.class
        );
        ItemRequest itemRequest = queryItemRequest.setParameter(
                "description",
                itemRequestPostDto.getDescription()
        ).getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestPostDto.getDescription()));
        assertThat(itemRequest.getCreated(), notNullValue());
        assertThat(itemRequest.getRequestor(), notNullValue());
    }

    private UserDto makeUserDto(String email, String name) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);

        return dto;
    }

    private ItemRequestPostDto makeItemRequestPostDto(String description) {
        return new ItemRequestPostDto(
                description
        );
    }
}
