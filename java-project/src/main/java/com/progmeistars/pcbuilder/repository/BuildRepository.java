package com.progmeistars.pcbuilder.repository;

import com.progmeistars.pcbuilder.entity.BuildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildRepository extends JpaRepository<BuildEntity, Long> {
}
