package com.openframe.openframe.domain.repository;

import com.openframe.openframe.domain.entity.Index;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexRepository extends JpaRepository<Index, Long> {
    // 필요 시 커스텀 메서드 추가 가능
}
