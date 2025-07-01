package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    @Query("""
            select r from ItemRequest r
            where r.requestor.id <> :requestorId
            order by r.created desc
            """)
    List<ItemRequest> findAllByOtherRequests(@Param("requestorId") Long requestorId);
}
