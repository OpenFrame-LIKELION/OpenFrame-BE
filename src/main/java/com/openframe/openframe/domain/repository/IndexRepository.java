package com.openframe.openframe.domain.repository;

import com.openframe.openframe.domain.entity.Index;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Long> {

    List<Index> findAllByChatId(Long chatId);
}
