package com.beyond.meongnyang.market.repository;

import com.beyond.meongnyang.market.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}