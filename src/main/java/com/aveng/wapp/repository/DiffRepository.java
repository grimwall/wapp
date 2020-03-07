package com.aveng.wapp.repository;

/*
 * Diff repo
 *
 * @author apaydin
 */

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aveng.wapp.domain.DiffEntity;

@Repository
public interface DiffRepository extends JpaRepository<DiffEntity, String> {

    Optional<DiffEntity> findByDiffId(long diffId);
}
