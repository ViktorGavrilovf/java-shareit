package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.dto.request.ItemRequestWithAnswersDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemRequestDto dto) {
        return service.addRequest(userId, dto);
    }

    @GetMapping
    public List<ItemRequestWithAnswersDto> getOwnRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithAnswersDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        return service.getRequestById(userId, requestId);
    }
}
